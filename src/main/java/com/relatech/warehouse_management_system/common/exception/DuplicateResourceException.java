package com.relatech.warehouse_management_system.common.exception;

public class DuplicateResourceException extends Exception {
    public DuplicateResourceException(String resource, String field, Object value) {
        super("Duplicate " + resource + " for " + field + ": " + value);
    }
}
