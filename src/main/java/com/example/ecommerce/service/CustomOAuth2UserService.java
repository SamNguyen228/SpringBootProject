package com.example.ecommerce.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email không tồn tại");
        }

        // Tìm hoặc tạo người dùng mới
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setPasswordHash("GoogleOAuth");
            user.setPhone("Chưa cập nhật");
            user.setAddress("Chưa cập nhật");
            user.setRoleId(2);
            user.setCreatedAt(LocalDateTime.now());

            user = userRepository.save(user);
        }

        // Gán quyền
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(
                user.getRoleId() == 1 ? "ROLE_ADMIN" : "ROLE_CUSTOMER"
        ));

        // Lưu thông tin thêm 
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getUserId());

        return new CustomOAuth2User(authorities, attributes, "email", user.getUserId());
    }
}
