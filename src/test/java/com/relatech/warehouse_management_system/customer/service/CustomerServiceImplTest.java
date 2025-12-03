package com.relatech.warehouse_management_system.customer.service;

import com.relatech.warehouse_management_system.common.exception.CustomerWithActiveOrdersException;
import com.relatech.warehouse_management_system.common.exception.DuplicateResourceException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.customer.dto.CustomerDTO;
import com.relatech.warehouse_management_system.customer.entity.Customer;
import com.relatech.warehouse_management_system.customer.mapper.CustomerMapper;
import com.relatech.warehouse_management_system.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl - Extended CRUD and Business Logic")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    @DisplayName("Create Customer - Duplicate Email Exception")
    void whenCreateDuplicateEmail_thenThrow() {
        CustomerDTO dto = CustomerDTO.builder().email("dupe@mail.com").taxCode("12345678901").build();
        when(repository.findByEmail("dupe@mail.com")).thenReturn(Optional.of(new Customer()));

        assertThrows(DuplicateResourceException.class, () -> service.createCustomer(dto));
    }

    @Test
    @DisplayName("Get Customer By Id - Success")
    void whenGetCustomerById_thenReturnDTO() throws ResourceNotFoundException {
        Customer customer = Customer.builder().id(1L).name("Name").build();
        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerDTO dto = CustomerDTO.builder().id(1L).name("Name").build();
        when(mapper.toDTO(customer)).thenReturn(dto);

        CustomerDTO result = service.getCustomerById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get Customer By Id - Not Found Exception")
    void whenGetCustomerByIdNotFound_thenThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCustomerById(99L));
    }

    @Test
    @DisplayName("Get All Customers Paged - Success")
    void whenGetAllCustomersPaged_thenReturnPage() {
        Customer c1 = Customer.builder().id(1L).build();
        Customer c2 = Customer.builder().id(2L).build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> pageEntity = new PageImpl<>(List.of(c1, c2), pageable, 2);
        when(repository.findAll(pageable)).thenReturn(pageEntity);
        when(mapper.toDTO(c1)).thenReturn(CustomerDTO.builder().id(1L).build());
        when(mapper.toDTO(c2)).thenReturn(CustomerDTO.builder().id(2L).build());

        Page<CustomerDTO> result = service.getAllCustomersPaged(pageable);
        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("Update Customer - Success")
    void whenUpdateCustomer_thenReturnUpdated() throws ResourceNotFoundException {
        CustomerDTO updateDto = CustomerDTO.builder().name("Updated").build();
        Customer existing = Customer.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDTO(existing)).thenReturn(updateDto);

        CustomerDTO result = service.updateCustomer(1L, updateDto);
        assertEquals("Updated", result.getName());
    }

    @Test
    @DisplayName("Delete Customer - With Active Orders Exception")
    void whenDeleteWithActiveOrders_thenThrow() {
        CustomerServiceImpl spy = spy(service);
        doReturn(true).when(spy).hasActiveOrders(anyLong());

        assertThrows(CustomerWithActiveOrdersException.class, () -> spy.deleteCustomer(1L));
    }

    @Test
    @DisplayName("Delete Customer - Success")
    void whenDeleteCustomer_thenInvokeRepositoryDelete() throws Exception {
        Customer c = Customer.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        doNothing().when(repository).delete(c);

        service.deleteCustomer(1L);

        verify(repository, times(1)).delete(c);
    }

    @Test
    @DisplayName("Search Customers - Null or Empty Term Returns All")
    void whenSearchNullOrEmptyReturnsAll() {
        List<CustomerDTO> allDtos = List.of(CustomerDTO.builder().id(1L).build());
        when(repository.findAll()).thenReturn(List.of(new Customer()));
        when(mapper.toDTO(any(Customer.class))).thenReturn(allDtos.get(0));

        List<CustomerDTO> resultNull = service.searchCustomers(null);
        List<CustomerDTO> resultEmpty = service.searchCustomers("");

        assertEquals(1, resultNull.size());
        assertEquals(1, resultEmpty.size());
    }

        @Test
        @DisplayName("Create Customer - Success if no duplicates")
        void whenCreateCustomerWithNoDuplicates_thenPersistsAndReturnsDTO() throws Exception {
            CustomerDTO dto = CustomerDTO.builder()
                    .name("Mario")
                    .surname("Rossi")
                    .shippingAddress("Via Roma 1")
                    .billingAddress("Via Milano 2")
                    .email("mail@rossi.com")
                    .taxCode("MRRSSM80A01H501X")
                    .build();

            when(repository.findByEmail("mail@rossi.com")).thenReturn(Optional.empty());
            when(repository.findByTaxCode("MRRSSM80A01H501X")).thenReturn(Optional.empty());

            Customer entity = Customer.builder()
                    .name("Mario")
                    .surname("Rossi")
                    .shippingAddress("Via Roma 1")
                    .billingAddress("Via Milano 2")
                    .email("mail@rossi.com")
                    .taxCode("MRRSSM80A01H501X")
                    .build();

            when(mapper.toEntity(dto)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(entity);
            when(mapper.toDTO(entity)).thenReturn(dto);

            CustomerDTO result = service.createCustomer(dto);
            assertEquals("Mario", result.getName());
            verify(repository).save(entity);
        }

    @Test
    @DisplayName("Create Customer - Duplicate Email")
    void whenCreateCustomerWithDuplicateEmail_thenThrows() {
        CustomerDTO dto = CustomerDTO.builder()
                .name("Mario")
                .surname("Rossi")
                .shippingAddress("Via Roma 1")
                .billingAddress("Via Milano 2")
                .email("mail@rossi.com")
                .taxCode("MRRSSM80A01H501X")
                .build();

        when(repository.findByEmail("mail@rossi.com")).thenReturn(Optional.of(new Customer()));

        assertThrows(DuplicateResourceException.class, () -> service.createCustomer(dto));
    }


    @Test
        @DisplayName("Create Customer - Duplicate TaxCode")
        void whenCreateCustomerWithDuplicateTaxCode_thenThrows() {
            CustomerDTO dto = CustomerDTO.builder()
                    .name("Mario")
                    .surname("Rossi")
                    .shippingAddress("Via Roma 1")
                    .billingAddress("Via Milano 2")
                    .email("mail@rossi.com")
                    .taxCode("MRRSSM80A01H501X")
                    .build();

            when(repository.findByEmail("mail@rossi.com")).thenReturn(Optional.empty());
            when(repository.findByTaxCode("MRRSSM80A01H501X")).thenReturn(Optional.of(new Customer()));

            assertThrows(DuplicateResourceException.class, () -> service.createCustomer(dto));
        }

        @Test
        @DisplayName("Get All Customers - Returns mapped list")
        void whenGetAllCustomers_thenReturnsMappedList() {
            Customer c = Customer.builder()
                    .name("Mario")
                    .surname("Rossi")
                    .shippingAddress("Via Roma 1")
                    .billingAddress("Via Milano 2")
                    .email("mail@rossi.com")
                    .taxCode("MRRSSM80A01H501X")
                    .build();
            when(repository.findAll()).thenReturn(List.of(c));
            when(mapper.toDTO(c)).thenReturn(CustomerDTO.builder().name("Mario").build());

            List<CustomerDTO> dtos = service.getAllCustomers();
            assertEquals(1, dtos.size());
            assertEquals("Mario", dtos.get(0).getName());
            verify(mapper).toDTO(c);
        }

        @Test
        @DisplayName("Search Customers - Null Term Returns All")
        void whenSearchCustomersWithNullTerm_thenGetAllCustomersCalled() {
            Customer c = Customer.builder().name("Mario").build();
            when(repository.findAll()).thenReturn(List.of(c));
            when(mapper.toDTO(c)).thenReturn(CustomerDTO.builder().name("Mario").build());

            List<CustomerDTO> result = service.searchCustomers(null);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Search Customers - Matching Term")
        void whenSearchCustomersWithTerm_thenSearchByTermCalled() {
            Customer c = Customer.builder().name("Mario").build();
            when(repository.searchByTerm("Mario")).thenReturn(List.of(c));
            when(mapper.toDTO(c)).thenReturn(CustomerDTO.builder().name("Mario").build());

            List<CustomerDTO> result = service.searchCustomers("Mario");
            assertEquals(1, result.size());
            assertEquals("Mario", result.get(0).getName());
        }
    }


