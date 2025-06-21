package com.example.ecommerce.controller;

import com.example.ecommerce.model.ChatMessage;
import com.example.ecommerce.repository.ChatMessageRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/admin/chat-messages")
public class ChatMangeController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<ChatMessage> messagePage = chatMessageRepository.findAll(pageable);

        model.addAttribute("messages", messagePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", messagePage.getTotalPages());
        return "view/admin/chatManage/message-list";
    }

    @GetMapping("/reply/{id}")
    public String showReplyForm(@PathVariable("id") Integer id, Model model) {
        ChatMessage message = chatMessageRepository.findById(id).orElse(null);
        model.addAttribute("message", message);
        return "view/admin/chatManage/reply-message";
    }

    @PostMapping("/reply/{id}")
    public String submitReply(@PathVariable("id") Integer id,
                              @RequestParam("reply") String reply,
                              Model model) {
        ChatMessage message = chatMessageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setReply(reply);
            chatMessageRepository.save(message);

            try {
                sendReplyToCustomer(message.getEmail(), reply);
                model.addAttribute("successMessage", "Câu trả lời đã được gửi thành công!");
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "Gửi email thất bại: " + ex.getMessage());
            }
        }
        return "redirect:/admin/chat-messages";
    }

    @GetMapping("/view/{id}")
    public String viewMessage(@PathVariable("id") Integer id, Model model) {
        ChatMessage message = chatMessageRepository.findById(id).orElse(null);
        model.addAttribute("message", message);
        return "view/admin/chatManage/message-view";
    }

    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") Integer id) {
        chatMessageRepository.deleteById(id);
        return "redirect:/admin/chat-messages";
    }

    private void sendReplyToCustomer(String customerEmail, String replyMessage) throws MessagingException {
        String fromEmail = env.getProperty("spring.mail.username");

        String subject = "Tin nhắn của bạn đã được trả lời";
        String body = "<p>Kính gửi khách hàng,</p>" +
                      "<p>Chúng tôi xin thông báo rằng tin nhắn của bạn đã được phản hồi như sau:</p>" +
                      "<p><b>" + replyMessage + "</b></p>" +
                      "<p>Trân trọng,</p>" +
                      "<p>Đội ngũ hỗ trợ khách hàng</p>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(customerEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        try {
            helper.setFrom(fromEmail, "Apple Store");
            mailSender.send(mimeMessage);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Lỗi encoding tên người gửi: " + e.getMessage());
        }
        mailSender.send(mimeMessage);
    }
}
