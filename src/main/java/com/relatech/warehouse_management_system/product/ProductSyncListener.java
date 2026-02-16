package com.relatech.warehouse_management_system.product;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.relatech.warehouse_management_system.product.dto.ProductDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSyncListener {

    private final ProductMirrorRepository repository;

    @RabbitListener(queues = RabbitReceivingConfig.RECEIVING_QUEUE)
    public void handleProductSync(ProductDto productDto) {
        log.info("Sincronizzazione prodotto ricevuta: {}", productDto.getCode());
        
        ProductMirror mirror = new ProductMirror(
            productDto.getCode(),
            productDto.getName(),
            LocalDateTime.now(),
            productDto.getCategory()
        );

        // Save o Update (Idempotenza)
        repository.save(mirror);
    }
}
