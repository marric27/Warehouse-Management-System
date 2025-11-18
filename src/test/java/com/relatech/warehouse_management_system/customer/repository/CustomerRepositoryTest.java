package com.relatech.warehouse_management_system.customer.repository;

import com.relatech.warehouse_management_system.customer.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("CustomerRepository - CRUD and Query Tests")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    @DisplayName("Save Customer - Success")
    void whenSaveCustomer_thenCustomerIsPersisted() {
        Customer customer = Customer.builder()
                .name("Mario")
                .surname("Rossi")
                .shippingAddress("Via Roma")
                .billingAddress("Via Milano")
                .email("mario@rossi.com")
                .taxCode("MRRSSM80A01H501X").build();

        Customer saved = repository.save(customer);

        assertNotNull(saved.getId());
        assertEquals("Mario", saved.getName());
    }

    @Test
    @DisplayName("Find by Email - Existing Customer")
    void whenFindByEmailWithExistingCustomer_thenReturnCustomer() {
        Customer customer = Customer.builder()
                .name("Anna")
                .surname("Verdi")
                .shippingAddress("Via Po")
                .billingAddress("Via Dora")
                .email("anna@verdi.com")
                .taxCode("ANNVRD80A01H501X").build();
        repository.save(customer);

        Optional<Customer> found = repository.findByEmail("anna@verdi.com");
        assertTrue(found.isPresent());
        assertEquals("Anna", found.get().getName());
    }

    @Test
    @DisplayName("Find by TaxCode - Non Existing")
    void whenFindByTaxCodeWithNonExistingCustomer_thenReturnEmpty() {
        Optional<Customer> found = repository.findByTaxCode("NOPE123");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Search By Term - Matching Name")
    void whenSearchByTermWithPartialName_thenReturnResults() {
        Customer customer = Customer.builder()
                .name("Gianni")
                .surname("Neri")
                .shippingAddress("Via Lago")
                .billingAddress("Via Mare")
                .email("gianni@neri.com")
                .taxCode("GIANNER76A01H501X").build();
        repository.save(customer);

        List<Customer> results = repository.searchByTerm("gianni");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c -> c.getName().equals("Gianni")));
    }

    @Test
    @DisplayName("Delete Customer - Success")
    void whenDeleteCustomer_thenCustomerIsRemoved() {
        Customer customer = Customer.builder()
                .name("Sara")
                .surname("Blu")
                .shippingAddress("Via Cielo")
                .billingAddress("Via Terra")
                .email("sara@blu.com")
                .taxCode("SARBLU80A01H501X").build();
        Customer saved = repository.save(customer);

        repository.deleteById(saved.getId());
        Optional<Customer> found = repository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
    @Test
    @DisplayName("Find By Email - Success")
    void whenFindByEmail_thenReturn() {
        Customer c = Customer.builder()
                .name("Test")
                .surname("User")
                .shippingAddress("A")
                .billingAddress("B")
                .email("test@example.com")
                .taxCode("01234567890")
                .build();
        repository.save(c);

        Optional<Customer> found = repository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Search By Term - Returns Matching Customers")
    void whenSearchByTerm_thenReturnMatches() {
        Customer c = Customer.builder()
                .name("Mario")
                .surname("Rossi")
                .shippingAddress("Via Roma")
                .billingAddress("Via Milano")
                .email("mario@rossi.com")
                .taxCode("MRRSSM80A01H501X")
                .build();
        repository.save(c);

        List<Customer> results = repository.searchByTerm("mario");
        assertFalse(results.isEmpty());
    }
}

