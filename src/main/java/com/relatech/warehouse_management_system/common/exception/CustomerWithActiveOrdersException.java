package com.relatech.warehouse_management_system.common.exception;

public class CustomerWithActiveOrdersException extends Exception {
    public CustomerWithActiveOrdersException(Long customerId) {
        super("Cannot delete customer with ID " + customerId + ": there are active orders.");
    }
}
