package com.relatech.warehouse_management_system.goodsIn.exception;

public class CannotAssignItemToGrnClosedException extends Exception {
    public CannotAssignItemToGrnClosedException(Object id) {
        super("Cant assign item to Grn " + id + " in Closed state");
    }
}
