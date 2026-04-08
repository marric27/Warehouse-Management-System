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
                    3. Usa 'createGrn' quando l'utente chiede esplicitamente di creare/ricevere una nuova entrata merci (GRN) e sono presenti i dati minimi richiesti.
                    4. Usa 'createGrnItem' solo per aggiungere una riga a un GRN esistente: prima verifica che ci siano sempre 'grnCode' + dati item necessari.
                    5. Usa 'createSalesOrder' quando l'utente chiede di creare un ordine cliente: richiedi sempre 'customerCode' + almeno una riga d'ordine completa.
                    6. Usa 'createProduct' quando l'utente chiede di creare un nuovo prodotto.
                    7. Per 'createProduct' i campi minimi obbligatori sono 'name' e 'category'; se manca uno dei due chiedi solo il dato mancante prima di invocare il tool.
                    8. Le categorie valide sono: CONTROLLED_DRUG, REFRIGERATED, FLAMMABLE, STANDARD. Se l'utente fornisce un valore diverso, chiedi di correggerlo.
                    9. Se mancano campi obbligatori per chiamare un tool, chiedi in italiano solo i dati mancanti e non invocare il tool finché non li ricevi.
                    10. Non rispondere con codice JSON. Usa i dati ricevuti dallo strumento per scrivere una frase naturale in italiano.
                    11. Non inventare codici/ID o altri valori non presenti nei dati utente o restituiti dai tool.
                    12. In caso di errore di validazione, riporta un messaggio chiaro in italiano indicando cosa correggere.
                    13. Dopo una creazione riuscita, conferma sempre all'utente l'entità creata riportando il 'code' o l''id' restituito dal tool.
                    14. Se non trovi il prodotto non inventare.
                    15. Non inventare informazioni ma limitati a quelle contenute nel database.
                    """)
                .build();
    }

    @GetMapping("/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message") String message) {
        log.info("Message >>> {}", message);
        try {
            String response = chatClient.prompt()
                    .user(message)
                    .tools("getProductDetails", "createProduct", "createGrn", "createGrnItem", "createSalesOrder")
                    .call()
                    .content();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while generating AI response", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing the AI request.");
        }
    }
}
