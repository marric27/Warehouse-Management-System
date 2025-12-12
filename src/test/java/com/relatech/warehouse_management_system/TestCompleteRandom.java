package com.relatech.warehouse_management_system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.controller.CheckGoodsInController;
import com.relatech.warehouse_management_system.goodsIn.dto.*;
import com.relatech.warehouse_management_system.outbound.dto.*;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestCompleteRandom {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    private final Faker faker = new Faker();

    // =================================================================================
    // GENERIC POST METHODS
    // =================================================================================
    private <T> T performPost(String url, Object body, Class<T> returnType) throws Exception {
        String response = mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, returnType);
    }

    private <T> T performPost(String url, Object body, TypeReference<T> typeRef) throws Exception {
        String response = mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, typeRef);
    }

    // =================================================================================
    // CREATE SINGLE OBJECTS
    // =================================================================================
    private SlotDto createSlot() throws Exception {
        SlotDto slot = new SlotDto(null, null, Category.STANDARD, 100, null, null);
        return performPost("/slots", slot, SlotDto.class);
    }

    private GrnDto createGrn() throws Exception {
        GrnDto grn = new GrnDto(null, null, "Supplier", LocalDate.now(), null, null);
        return performPost("/receiving/grns", grn, GrnDto.class);
    }

    private GrnItemDto createGrnItem(Long grnId, String productCode, int quantity) throws Exception {
        GrnItemDto item = new GrnItemDto(null, null, productCode,
                quantity, quantity, quantity, 0, State.OPEN, grnId, null);
        return performPost("/receiving/grns/" + grnId + "/items", item, GrnItemDto.class);
    }

    private CustomerDto createCustomer(int index) throws Exception {
        CustomerDto customer = new CustomerDto(
                null,
                faker.name().firstName() + index,
                faker.name().lastName() + index,
                faker.address().streetAddress(),
                faker.company().name(),
                faker.internet().emailAddress(),
                faker.idNumber().valid(),
                null
        );
        return performPost("/sales-order/customer", customer, CustomerDto.class);
    }

    private OrderDto createOrder(CustomerDto customer, String productCode, int quantity) throws Exception {
        SalesOrderLineDto line = SalesOrderLineDto.builder()
                .salesOrderNumber("SO-" + faker.number().digits(3))
                .productCode(productCode)
                .quantity(quantity)
                .status(OrderState.OPEN)
                .build();

        OrderDto order = new OrderDto(null, null, LocalDate.now(),
                customer.getCustomerCode(), OrderState.OPEN, List.of(line));

        return performPost("/sales-order/create-order/" + customer.getId(), order, OrderDto.class);
    }

    // =================================================================================
    // BATCH METHODS
    // =================================================================================
    private List<String> generateProducts(int count) {
        List<String> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add("PRD-" + faker.number().digits(3));
        }
        return products;
    }

    private List<GrnItemDto> createItemsForGrns(List<GrnDto> grns, List<String> products, int itemsPerGrn) throws Exception {
        List<GrnItemDto> allItems = new ArrayList<>();
        for (GrnDto grn : grns) {
            for (int i = 0; i < itemsPerGrn; i++) {
                String product = products.get(i % products.size());
                int qty = faker.number().numberBetween(20, 100);
                allItems.add(createGrnItem(grn.getId(), product, qty));
            }
        }
        return allItems;
    }

//    private List<GrnItemDto> createItemsForGrnsDistinct(List<GrnDto> grns, List<String> products, int itemsPerGrn) throws Exception {
//        List<GrnItemDto> allItems = new ArrayList<>();
//        Random random = new Random();
//
//        for (GrnDto grn : grns) {
//            // Copia dei prodotti e mescola
//            List<String> shuffledProducts = new ArrayList<>(products);
//            Collections.shuffle(shuffledProducts, random);
//
//            // Prendi i primi 'itemsPerGrn' prodotti per questo GRN
//            for (int i = 0; i < itemsPerGrn && i < shuffledProducts.size(); i++) {
//                String product = shuffledProducts.get(i);
//                int qty = faker.number().numberBetween(20, 100);
//                allItems.add(createGrnItem(grn.getId(), product, qty));
//            }
//        }
//        return allItems;
//    }

    private void receiveAndPutawayWithMinQty(GrnItemDto item, SlotDto slot, int minQty) throws Exception {
        int qty = Math.max(minQty, item.getReceivedQty());

        CheckingInfoDto checking = CheckingInfoDto.builder()
                .batchNumber("BN-" + faker.number().digits(5))
                .expirationDate(LocalDate.now().plusDays(faker.number().numberBetween(10, 60)))
                .quantity(qty)
                .state(State.OPEN)
                .build();

        StockUnitDto stock = StockUnitDto.builder()
                .batchNumber(checking.getBatchNumber())
                .expirationDate(checking.getExpirationDate())
                .productCode(item.getProductCode())
                .quantity(qty)
                .category(Category.STANDARD)
                .build();

        CheckGoodsInController.CreateCheckingInfoRequest req = new CheckGoodsInController.CreateCheckingInfoRequest();
        req.setCheckingInfo(checking);
        req.setStockUnit(stock);

        GrnItemDto updated = performPost(
                "/check-goods-in/" + item.getId() + "/checking-info",
                req,
                GrnItemDto.class
        );

        for (CheckingInfoDto checkingInfoDto : updated.getCheckingInfoList()) {
            performPost(
                    "/putaway/" + checkingInfoDto.getId() + "/assignToSlot/" + slot.getId(),
                    null,
                    GrnItemDto.class
            );
        }
    }

    private void receiveAndPutawayAllWithMinQty(List<GrnItemDto> items, List<SlotDto> slots, Map<String, Integer> minQtyPerProduct) throws Exception {
        int index = 0;
        for (GrnItemDto item : items) {
            SlotDto slot = slots.get(index % slots.size());
            int minQty = minQtyPerProduct.getOrDefault(item.getProductCode(), item.getReceivedQty());
            receiveAndPutawayWithMinQty(item, slot, minQty);
            index++;
        }
    }

    private List<PickListDto> generatePicklists(List<OrderDto> orders) throws Exception {
        List<PickListDto> finalList = new ArrayList<>();
        for (OrderDto o : orders) {
            List<PickListDto> partial = performPost(
                    "/picklists/release",
                    List.of(o.getId()),
                    new TypeReference<List<PickListDto>>() {}
            );
            finalList.addAll(partial);
        }
        return finalList;
    }

    // =================================================================================
// CREA ITEMS DIVERSI PER OGNI GRN
// =================================================================================
    private List<GrnItemDto> createItemsForGrnsDistinct(List<GrnDto> grns, List<String> products, int itemsPerGrn) throws Exception {
        List<GrnItemDto> allItems = new ArrayList<>();
        Random random = new Random();

        for (GrnDto grn : grns) {
            // Copia dei prodotti e mescola
            List<String> shuffledProducts = new ArrayList<>(products);
            Collections.shuffle(shuffledProducts, random);

            // Prendi i primi 'itemsPerGrn' prodotti per questo GRN
            for (int i = 0; i < itemsPerGrn && i < shuffledProducts.size(); i++) {
                String product = shuffledProducts.get(i);
                int qty = faker.number().numberBetween(20, 100);
                allItems.add(createGrnItem(grn.getId(), product, qty));
            }
        }
        return allItems;
    }

    // =================================================================================
// CREA ORDINE CON PIU LINEE
// =================================================================================
    private OrderDto createOrderWithMultipleLines(CustomerDto customer, List<String> products, int maxLines) throws Exception {
        Random random = new Random();
        int linesCount = random.nextInt(maxLines) + 1; // 1..maxLines linee
        List<SalesOrderLineDto> lines = new ArrayList<>();

        Collections.shuffle(products, random);
        for (int i = 0; i < linesCount && i < products.size(); i++) {
            String productCode = products.get(i);
            int qty = faker.number().numberBetween(1, 15);

            lines.add(SalesOrderLineDto.builder()
                    .salesOrderNumber("SO-" + faker.number().digits(3))
                    .productCode(productCode)
                    .quantity(qty)
                    .status(OrderState.OPEN)
                    .build());
        }

        OrderDto order = new OrderDto(null, null, LocalDate.now(),
                customer.getCustomerCode(), OrderState.OPEN, lines);

        return performPost("/sales-order/create-order/" + customer.getId(), order, OrderDto.class);
    }

    // =================================================================================
    // TEST PRINCIPALE
    // =================================================================================
    @Test
    void massCreationRandomTest() throws Exception {

        // 1) Slot
        List<SlotDto> slots = new ArrayList<>();
        for (int i = 0; i < 5; i++) slots.add(createSlot());

        // 2) GRN
        List<GrnDto> grns = new ArrayList<>();
        for (int i = 0; i < 3; i++) grns.add(createGrn());

        // 3) Prodotti
        List<String> products = generateProducts(5);

        // 4) GRN items distinti
        List<GrnItemDto> items = createItemsForGrnsDistinct(grns, products, 4);

        // 5) Clienti
        List<CustomerDto> customers = new ArrayList<>();
        for (int i = 0; i < 10; i++) customers.add(createCustomer(i));

        // 6) Ordini
//        List<OrderDto> orders = new ArrayList<>();
//        for (CustomerDto c : customers) {
//            String product = products.get(faker.number().numberBetween(0, products.size()));
//            int qty = faker.number().numberBetween(5, 15);
//            orders.add(createOrder(c, product, qty));
//        }
        // 6) Ordini con più linee
        List<OrderDto> orders = new ArrayList<>();
        for (CustomerDto c : customers) {
            orders.add(createOrderWithMultipleLines(c, products, 3)); // max 3 linee per ordine
        }

        // 7) Calcola quantità minima per prodotto
        Map<String, Integer> minQtyPerProduct = new HashMap<>();
        for (OrderDto order : orders) {
            for (SalesOrderLineDto line : order.getSalesOrderLineList()) {
                // somma tutte le richieste di quel prodotto
                minQtyPerProduct.merge(line.getProductCode(), line.getQuantity(), Integer::sum);
            }
        }

        // 8) Crea GRNItem con quantità >= minQtyPerProduct
        //List<GrnItemDto> items = new ArrayList<>();
        for (String product : products) {
            int qtyNeeded = minQtyPerProduct.getOrDefault(product, faker.number().numberBetween(5, 15));
            // Crea GRNItem con quantità sufficiente
            for (GrnDto grn : grns) {
                items.add(createGrnItem(grn.getId(), product, qtyNeeded));
            }
        }

        // 8) CheckGoodsIn + Putaway con quantità minima
        receiveAndPutawayAllWithMinQty(items, slots, minQtyPerProduct);

        // 9) Picklists
        List<PickListDto> picklists = generatePicklists(orders);

        // 10) Stampa risultato
        /*picklists.forEach(pl -> {
            System.out.println("===========================================");
            System.out.println("PICKLIST ID: " + pl.getId() + " | CODE: " + pl.getCode());
            System.out.println("CUSTOMER: " + pl.getCustomerCode());
            System.out.println("ITEMS:");
            pl.getPickListItemList().forEach(item -> {
                System.out.println("  ---------------------------------------");
                System.out.println("  Product Code       : " + item.getProductCode());
                System.out.println("  Quantity           : " + item.getQuantity());
                System.out.println("  Slot Code          : " + item.getSlotCode());
                System.out.println("  Sales Order Code   : " + item.getSalesOrderCode());
                System.out.println("  Sales Order Line   : " + item.getSalesOrderLineNumber());
            });
            System.out.println("===========================================\n");
        });*/

        // 10) Stampa completa degli oggetti creati
        System.out.println("\n\n\n========= SLOT =========");
        slots.forEach(s -> System.out.println("Slot ID: " + s.getId() + ", Capacity: " + s.getCapacity()));

        System.out.println("\n========= GRN =========");
        grns.forEach(g -> System.out.println("GRN ID: " + g.getId() + ", Supplier: " + g.getSupplier()));

        System.out.println("\n========= GRN ITEMS =========");
        items.forEach(i -> System.out.println("GRNItem ID: " + i.getId() + ", Product: " + i.getProductCode() + ", ReceivedQty: " + i.getReceivedQty() + ", GRN ID " + i.getGrnId()));

        System.out.println("\n========= CUSTOMERS =========");
        customers.forEach(c -> System.out.println("Customer ID: " + ", Name: " + c.getName() + " " + c.getSurname()));

        System.out.println("\n========= ORDERS =========");
        orders.forEach(o -> {
            System.out.println("Order ID: " + o.getId() + ", Customer: " + o.getCustomerCode());
            o.getSalesOrderLineList().forEach(line ->
                    System.out.println("   Line: " + line.getSalesOrderNumber() + ", Product: " + line.getProductCode() + ", Qty: " + line.getQuantity()));
        });

        System.out.println("\n========= PICKLISTS =========");
        picklists.forEach(pl -> {
            System.out.println("PickList ID: " + pl.getId() + ", Customer: " + pl.getCustomerCode());
            pl.getPickListItemList().forEach(item -> {
                System.out.println("   Item -> Product: " + item.getProductCode() +
                        ", Qty: " + item.getQuantity() +
                        ", Slot: " + item.getSlotCode() +
                        ", Order: " + item.getSalesOrderCode() +
                        ", Line: " + item.getSalesOrderLineNumber());
            });
        });
        System.out.println("\n\n=========  =========\n\n\n\n");

    }
}
