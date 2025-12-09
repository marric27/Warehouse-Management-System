package com.relatech.warehouse_management_system.goodsIn.exception;

public class GrnWithItemsException extends Exception {
    public GrnWithItemsException(String grnId) {
        super("Cannot delete GRN " + grnId + ": it contains associated items");
    }
}