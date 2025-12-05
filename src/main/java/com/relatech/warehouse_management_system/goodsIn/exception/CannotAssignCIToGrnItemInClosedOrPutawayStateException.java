package com.relatech.warehouse_management_system.goodsIn.exception;

public class CannotAssignCIToGrnItemInClosedOrPutawayStateException extends Exception {
    public CannotAssignCIToGrnItemInClosedOrPutawayStateException(Long grnItemId) {
        super("Cant assign checking info to GrnItem " + grnItemId + " in Closed or Putaway state");
    }
}
