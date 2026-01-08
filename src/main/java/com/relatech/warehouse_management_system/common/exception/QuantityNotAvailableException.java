package com.relatech.warehouse_management_system.common.exception;

public class QuantityNotAvailableException extends Exception {
    public QuantityNotAvailableException(int requestedQuantity, int availableQuantity) {
        super("Requested quantity " + requestedQuantity + " exceeds available quantity " + availableQuantity);
    }
}