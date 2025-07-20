package com.example.ecommerce.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.ChatMessage;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ChatMessageRepository;
import com.example.ecommerce.repository.UserRepository;

@RestController
@RequestMapping("/sendChat")
public class ChatRestController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    public Map<String, Object> saveMessage(@RequestBody Map<String, String> payload,
            Principal principal) {
        Map<String, Object> result = new HashMap<>();
        String content = payload.get("message");

        if (content == null || content.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Tin nhắn không được để trống");
            return result;
        }

        ChatMessage chat = new ChatMessage();
        chat.setMessage(content);
        chat.setCreatedAt(LocalDateTime.now());

        if (principal != null) {
            String username = principal.getName(); // username/email tu Spring Security
            chat.setName(username);

            // Giả sử bạn có UserRepository để lấy tên
            User user = userRepository.findByEmail(username);
            if (user != null) {
                chat.setName(user.getFullName()); // hoặc user.getName()
            } else {
                chat.setName("Ẩn danh");
            }

            chat.setEmail(username);
        }

        chatMessageRepository.save(chat);
        result.put("success", true);
        return result;
    }
}
