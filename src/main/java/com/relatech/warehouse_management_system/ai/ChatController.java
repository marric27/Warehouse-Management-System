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
                    Sei un assistente per il magazzino.
                    REGOLE CRITICHE:
                    1. Se l'utente chiede informazioni su un prodotto, DEVI usare lo strumento 'getProductDetails'.
                    2. Estrai il codice del prodotto (es. PRD-001) e passalo come parametro 'code'.
                    3. Non rispondere con codice JSON. Usa i dati ricevuti dallo strumento per scrivere una frase naturale in italiano.
                    4. Se non trovi il prodotto non inventare.
                    5. Non inventare informazioni ma limitati a quelle contenute nel database.
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
                    .content();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while generating AI response", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing the AI request.");
        }
    }
}