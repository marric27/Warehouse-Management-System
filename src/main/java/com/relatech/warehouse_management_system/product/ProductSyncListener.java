package com.relatech.warehouse_management_system.product;

import java.time.LocalDateTime;

import com.relatech.warehouse_management_system.product.entity.Product;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

        Product mirror = new Product(
            productDto.getCode(),
            productDto.getName(),
            LocalDateTime.now(),
            productDto.getCategory()
        );

        // Save o Update (Idempotenza)
        repository.save(mirror);
    }
}
