package com.relatech.warehouse_management_system.goodsIn.exception;

public class CannotAssignCIToGrnItemInClosedOrPutawayStateException extends Exception {
    public CannotAssignCIToGrnItemInClosedOrPutawayStateException(String grnItemCode) {
        super("Cant assign checking info to GrnItem " + grnItemCode + " in Closed or Putaway state");
    }
}
