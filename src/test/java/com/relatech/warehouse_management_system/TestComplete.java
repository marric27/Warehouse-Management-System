package com.relatech.warehouse_management_system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.common.util.State;
import com.relatech.warehouse_management_system.goodsIn.checkGoodsIn.controller.CheckGoodsInController;
import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.customer.entity.CustomerDto;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.outbound.dto.OrderDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListDto;
import com.relatech.warehouse_management_system.outbound.dto.PickListItemDto;
import com.relatech.warehouse_management_system.outbound.dto.SalesOrderLineDto;
import com.relatech.warehouse_management_system.product.dto.ProductDto;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestComplete {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // =================================================================================
    //  DEFAULT GENERIC POST METHOD
    // =================================================================================
    private <T> T performPost(String url, Object body, Class<T> returnType) throws Exception {
        String response = mockMvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, returnType);
    }

    private <T> T performPost(String url, Object body, TypeReference<T> typeRef) throws Exception {
        String response = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, typeRef);
    }

    private <T> T performGet(String url, Class<T> returnType) throws Exception {
        String response = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, returnType);
    }

    // =================================================================================
    //  STEP METHODS
    // =================================================================================
    private ProductDto createProduct(String productCode) throws Exception {
        ProductDto product = new ProductDto();
        product.setCode(productCode);
        product.setName("Product " + productCode);
        product.setCategory(Category.STANDARD);

        return performPost("/products", product, ProductDto.class);
    }


    private SlotDto createSlot() throws Exception {
        SlotDto slot = new SlotDto(null, null, 1, Category.STANDARD, 100, null, null);

        return performPost("/slots", slot, SlotDto.class);
    }

    private GrnDto createGrn() throws Exception {
        GrnDto grn = new GrnDto(null, null, "Supplier", LocalDate.now(), null, null);

        return performPost("/receiving/grns", grn, GrnDto.class);
    }

    private GrnItemDto createGrnItem(Long grnId, String productCode) throws Exception {
        GrnItemDto item = new GrnItemDto(null, null, productCode, 100, 100, 100, 0, State.OPEN, grnId, null);

        return performPost("/receiving/grns/" + grnId + "/items", item, GrnItemDto.class);
    }

    private GrnItemDto checkGoodsIn(Long itemId, String productCode) throws Exception {

        CheckingInfoDto checking = CheckingInfoDto.builder()
                .batchNumber("BN-2025A")
                .expirationDate(LocalDate.now().plusDays(10))
                .quantity(50)
                .state(State.OPEN)
                .build();

        StockUnitDto stock = StockUnitDto.builder()
                .batchNumber("BN-2025A")
                .expirationDate(LocalDate.now().plusDays(10))
                .productCode(productCode)
                .quantity(50)
                .category(Category.STANDARD)
                .build();

        CheckGoodsInController.CreateCheckingInfoRequest req = new CheckGoodsInController.CreateCheckingInfoRequest();

        req.setCheckingInfo(checking);
        req.setStockUnit(stock);

        return performPost("/check-goods-in/" + itemId + "/checking-info", req, GrnItemDto.class);
    }

    private final Faker faker = new Faker();

    private CustomerDto createCustomer(int index) throws Exception {
        CustomerDto customer = new CustomerDto(
                null,
                faker.name().firstName() + index,
                faker.name().lastName() + index,
                faker.address().streetAddress(),
                faker.company().name(),
                faker.internet().emailAddress(),
                faker.regexify("[A-Z0-9]{16}"),
                null
        );
        return performPost("/customers", customer, CustomerDto.class);
    }

    private OrderDto createOrder(CustomerDto customer, String productCode) throws Exception {
        SalesOrderLineDto line = SalesOrderLineDto.builder()
                .productCode(productCode)
                .quantity(10)
                .status(OrderState.OPEN)
                .build();

        OrderDto order = new OrderDto(null, null, LocalDate.now(), customer.getCustomerCode(), OrderState.OPEN, List.of(line));

        return performPost("/sales-order/create-order/" + customer.getId(), order, OrderDto.class);
    }

    private List<PickListDto> generatePicklist(List<Long> orderIds) throws Exception {
        return performPost("/picklists/release", orderIds, new TypeReference<List<PickListDto>>() {
                }
        );
    }

    private PickListItemDto getNextPickingItem(List<Long> pickListIds) throws Exception {
        String response = mockMvc.perform(post("/picking/next-item")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pickListIds))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        if (response.isBlank()) {
            return null;
        }

        return objectMapper.readValue(response, PickListItemDto.class);
    }


    // =================================================================================
    //  MAIN TEST
    // =================================================================================

    @Test
    void completeTest() throws Exception {
        // ===== 0) CREATE PRODUCTS =====
        List<String> products = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String productCode = "PRD-" + String.format("%03d", i);
            products.add(productCode);
            createProduct(productCode);
        }

        // ===== 1) CREATE SLOTS =====
        List<SlotDto> slots = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            slots.add(createSlot());// creo una slot per facilitare il test
        }

        // ===== 2) CREATE GRNs =====
        List<GrnDto> grns = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GrnDto grn = createGrn();

            // Genera 2-3 prodotti diversi per ogni GRN
            for (int j = 0; j < 2 + faker.random().nextInt(5); j++) {
                String productCode = products.get(faker.random().nextInt(products.size()));
                createGrnItem(grn.getId(), productCode);

            }

            // Ricarica il GRN completo con i suoi items
            grn = performGet("/receiving/grns/" + grn.getId(), GrnDto.class);
            grns.add(grn);
        }

        // ===== 3) CHECK GOODS IN =====
        for (GrnDto grn : grns) {
            for (GrnItemDto item : grn.getItems()) {
                GrnItemDto updated = checkGoodsIn(item.getId(), item.getProductCode());
                // PUTAWAY nel primo slot disponibile
                for (CheckingInfoDto checkingInfo : updated.getCheckingInfoList()) {
                    performPost("/putaway/" + checkingInfo.getId() + "/assignToSlot/" + slots.get(0).getId(),
                            null,
                            GrnItemDto.class
                    );
                }
            }
        }

        // ===== 4) CREATE CUSTOMERS =====
        List<CustomerDto> customers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            customers.add(createCustomer(i));
        }

        // ===== 5) CREATE ORDERS =====
        List<OrderDto> orders = new ArrayList<>();
        for (CustomerDto customer : customers) {
            // ogni ordine ha linee diverse
            for (int i = 0; i < 2 + faker.random().nextInt(5); i++) {
                String productCode = products.get(faker.random().nextInt(products.size()));
                orders.add(createOrder(customer, productCode));
            }
        }

        // ===== 6) GENERATE PICKLIST =====
        List<Long> orderIds = orders.stream().map(OrderDto::getId).toList();
        List<PickListDto> picklists = generatePicklist(orderIds);

        // ===== 7) PRINT RESULTS =====
        System.out.println("\n===== PICKLISTS =====");
        for (PickListDto pl : picklists) {
            System.out.println("PICKLIST ID: " + pl.getId() + ", Customer: " + pl.getCustomerCode() + ", ReleaseNumber: " + pl.getReleaseNumber());
            for (var item : pl.getPickListItemList()) {
                System.out.println("  Product: " + item.getProductCode() + ", Qty: " + item.getQuantity()
                        + ", Slot: " + item.getSlotCode() + ", Order: " + item.getSalesOrderCode());
            }
            System.out.println("----------------------------------");
        }

        // ===== 5) CREATE ORDERS =====
        orders = new ArrayList<>();
        for (CustomerDto customer : customers) {
            // ogni ordine ha linee diverse
            for (int i = 0; i < 2 + faker.random().nextInt(5); i++) {
                String productCode = products.get(faker.random().nextInt(products.size()));
                orders.add(createOrder(customer, productCode));
            }
        }

        // ===== 6) GENERATE PICKLIST =====
        orderIds = orders.stream().map(OrderDto::getId).toList();
        picklists = generatePicklist(orderIds);

        // ===== 7) PRINT RESULTS =====
        System.out.println("\n===== PICKLISTS =====");
        for (PickListDto pl : picklists) {
            System.out.println("PICKLIST ID: " + pl.getId() + ", Customer: " + pl.getCustomerCode() + ", ReleaseNumber: " + pl.getReleaseNumber());
            for (var item : pl.getPickListItemList()) {
                System.out.println("  Product: " + item.getProductCode() + ", Qty: " + item.getQuantity()
                        + ", Slot: " + item.getSlotCode() + ", Order: " + item.getSalesOrderCode());
            }
            System.out.println("----------------------------------");
        }

        // ===== 8) GET NEXT PICKING ITEM =====
        List<Long> pickListIds = new ArrayList<>();
        for (int i = 0; i < picklists.size(); i++) {
            pickListIds.add((long) (i + 1));
        }

        System.out.println("\n===== NEXT PICK ITEM =====");
        for (int i = 0; i < 5; i++) {
            PickListItemDto item = getNextPickingItem(pickListIds);
            if (item == null) break;

            System.out.println("NEXT → " + item);
        }

    }

}
