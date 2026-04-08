package com.relatech.warehouse_management_system.ai;


import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.service.GrnService;
import com.relatech.warehouse_management_system.goodsIn.receiving.service.ReceivingService;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.outbound.salesOrder.SalesOrderService;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.product.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Configuration
public class FunctionConfiguration {
    private final ProductService productService;
    private final GrnService grnService;
    private final ReceivingService receivingService;
    private final SalesOrderService salesOrderService;

    public record ProductCode(String code) {}
    public record ProductDetails(long id, String code, String name, Category category){}
    public record CreateProductInput(String name, Category category) {}

    public record CreateGrnInput(String supplier, String receivingDate) {}

    public record CreateGrnItemInput(
            String grnCode,
            String productCode,
            int expectedQty,
            int receivedQty,
            int compliantQty,
            int notCompliantQty
    ) {}

    public record CreateSalesOrderLineInput(String productCode, int quantity) {}

    public record CreateSalesOrderInput(String customerCode, List<CreateSalesOrderLineInput> lines) {}

    public record AiOperationResult(boolean success, Long id, String code, String state, String message) {}

    @Bean
    @Description("Recupera i dati tecnici di un prodotto tramite il suo codice")
    public Function<ProductCode, ProductDetails> getProductDetails() {
        return productCode -> {
            ProductDto product = null;
            try {
                product = productService.getProductByCode(productCode.code());
                log.info("product details for product code {}", productCode);
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (product != null) {
                return new ProductDetails(product.getId(), product.getCode(), product.getName(), product.getCategory());
            } else {
                return new ProductDetails(0, "Not Found", null, null);
            }
        };
    }

    @Bean
    @Description("Crea un nuovo prodotto con nome e categoria")
    public Function<CreateProductInput, AiOperationResult> createProduct() {
        return input -> {
            try {
                ProductDto request = ProductDto.builder()
                        .name(input.name())
                        .category(input.category())
                        .build();

                ProductDto created = productService.createProduct(request);
                log.info("prodotto creato con codice {}", created.getCode());
                return new AiOperationResult(
                        true,
                        created.getId(),
                        created.getCode(),
                        null,
                        "Prodotto creato con successo"
                );
            } catch (Exception e) {
                log.warn("Error while creating product", e);
                return new AiOperationResult(false, null, null, null, "Errore creazione prodotto: " + e.getMessage());
            }
        };
    }

    @Bean
    @Description("Crea una Goods Receipt Note (GRN) con supplier e data ricezione opzionale (YYYY-MM-DD)")
    public Function<CreateGrnInput, AiOperationResult> createGrn() {
        return input -> {
            try {
                GrnDto request = GrnDto.builder()
                        .supplier(input.supplier())
                        .receivingDate(input.receivingDate() == null || input.receivingDate().isBlank()
                                ? null
                                : LocalDate.parse(input.receivingDate()))
                        .build();

                GrnDto created = receivingService.createGRN(request);
                return new AiOperationResult(
                        true,
                        created.getId(),
                        created.getCode(),
                        created.getState() != null ? created.getState().name() : null,
                        "GRN creata con successo"
                );
            } catch (Exception e) {
                log.warn("Error while creating GRN", e);
                return new AiOperationResult(false, null, null, null, "Errore creazione GRN: " + e.getMessage());
            }
        };
    }

    @Bean
    @Description("Crea un item GRN per un GRN esistente (grnCode, productCode, quantità)")
    public Function<CreateGrnItemInput, AiOperationResult> createGrnItem() {
        return input -> {
            try {
                GrnItemDto request = GrnItemDto.builder()
                        .productCode(input.productCode())
                        .expectedQty(input.expectedQty())
                        .receivedQty(input.receivedQty())
                        .compliantQty(input.compliantQty())
                        .notCompliantQty(input.notCompliantQty())
                        .build();

                GrnItemDto created = receivingService.createItem(input.grnCode(), request);
                return new AiOperationResult(
                        true,
                        created.getId(),
                        created.getCode(),
                        created.getState() != null ? created.getState().name() : null,
                        "Item GRN creato con successo"
                );
            } catch (Exception e) {
                log.warn("Error while creating GRN item for GRN {}", input.grnCode(), e);
                return new AiOperationResult(false, null, null, null, "Errore creazione item GRN: " + e.getMessage());
            }
        };
    }

    @Bean
    @Description("Crea un Sales Order validando customer e prodotti")
    public Function<CreateSalesOrderInput, AiOperationResult> createSalesOrder() {
        return input -> {
            try {
                OrderDto request = OrderDto.builder()
                        .customerCode(input.customerCode())
                        .salesOrderLineList(input.lines().stream()
                                .map(line -> SalesOrderLineDto.builder()
                                        .productCode(line.productCode())
                                        .quantity(line.quantity())
                                        .build())
                                .toList())
                        .build();

                OrderDto created = salesOrderService.createOrderAndAssign(request);
                return new AiOperationResult(
                        true,
                        created.getId(),
                        created.getCode(),
                        created.getState() != null ? created.getState().name() : null,
                        "Sales Order creato con successo"
                );
            } catch (Exception e) {
                log.warn("Error while creating sales order", e);
                return new AiOperationResult(false, null, null, null, "Errore creazione Sales Order: " + e.getMessage());
            }
        };
    }
}
