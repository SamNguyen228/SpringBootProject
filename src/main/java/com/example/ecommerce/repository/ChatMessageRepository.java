/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.ecommerce.repository;

import com.example.ecommerce.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author fptshop
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
