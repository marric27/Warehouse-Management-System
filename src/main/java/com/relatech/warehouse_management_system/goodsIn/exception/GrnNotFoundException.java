package com.relatech.warehouse_management_system.goodsIn.exception;

public class GrnNotFoundException extends Exception {
    public GrnNotFoundException(Long id) {
        super("GRN not found with ID: " + id);
    }
    public GrnNotFoundException(String code) {
        super("GRN not found with code: " + code);
    }
}