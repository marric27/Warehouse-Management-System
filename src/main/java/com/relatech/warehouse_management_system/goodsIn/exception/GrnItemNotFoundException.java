package com.relatech.warehouse_management_system.goodsIn.exception;

public class GrnItemNotFoundException extends Exception {
    public GrnItemNotFoundException(Long id) {
        super("GRNItem not found with ID: " + id);
    }
    public GrnItemNotFoundException(String code) {
        super("GRN not found with code: " + code);
    }
}