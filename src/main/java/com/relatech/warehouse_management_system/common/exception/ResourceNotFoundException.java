package com.relatech.warehouse_management_system.common.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
}
