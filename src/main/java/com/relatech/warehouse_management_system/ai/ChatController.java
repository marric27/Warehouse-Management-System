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
                .defaultSystem("""
                        You are an AI assistant for warehouse products.
                        You must always use the getProductDetails tool before answering
                        any user question related to products.
                        If the tool does not return data, answer that the product was not found.
                        """)

                .build();
    }

    @GetMapping("/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message") String message) {
        log.info("message {}", message);
        try {
            String response = chatClient.prompt()
                    .user(message)
                    .tools("getProductDetails")
                    .call()
                    .content()
                    .toString();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while generating AI response", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing the AI request.");
        }
    }
}