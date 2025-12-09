package com.relatech.warehouse_management_system.goodsIn.exception;

public class InvalidStateTransitionException extends Exception {
    public InvalidStateTransitionException(Enum<?> from, Enum<?> to) {
        super("Invalid state transition: " + (from != null ? from.name() : "null") +
                " → " + (to != null ? to.name() : "null"));
    }
}