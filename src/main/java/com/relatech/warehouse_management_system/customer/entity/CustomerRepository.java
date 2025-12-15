package com.relatech.warehouse_management_system.customer.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

Optional<Customer> findByEmail(String email);

Optional<Customer> findByTaxCode(String taxCode);

    @Query("SELECT c FROM Customer c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(c.surname) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(c.shippingAddress) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(c.billingAddress) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Customer> searchByTerm(@Param("term") String term);

    Optional<Customer> findByCustomerCode(String customerCode);
}
