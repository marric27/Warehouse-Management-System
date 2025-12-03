package com.relatech.warehouse_management_system.goodsIn.exception;

public class UpdateEntityException extends Exception {
    public UpdateEntityException(Object id) {
        super("Cant update slot category cause contains a product");
    }
}
