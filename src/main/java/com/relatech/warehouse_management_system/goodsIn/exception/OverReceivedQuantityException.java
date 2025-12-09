package com.relatech.warehouse_management_system.goodsIn.exception;

public class OverReceivedQuantityException extends Exception {
    public OverReceivedQuantityException(String message) {
        super(message);
    }
}