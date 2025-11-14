package com.relatech.warehouse_management_system.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with identifier: " + id);
    }
}
