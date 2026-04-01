package com.relatech.warehouse_management_system.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are an AI assistant answering questions about different products")

                .build();
    }

    @GetMapping("/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message") String message) {
        log.info("message {}", message);
        try {
            String response = chatClient.prompt()
                    .user(message)
                    .functions("getProductDetails")
                    .call()
                    .content()
                    .toString();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}