package com.relatech.warehouse_management_system.goodsIn.exception;

public class GrnExceptions {

    public static class GrnNotFoundException extends Exception {
        public GrnNotFoundException(Long id) {
            super("GRN not found with ID: " + id);
        }
        public GrnNotFoundException(String code) {
            super("GRN not found with code: " + code);
        }
    }

    public static class GrnItemNotFoundException extends Exception {
        public GrnItemNotFoundException(Long id) {
            super("GRNItem not found with ID: " + id);
        }
    }

    public static class GrnWithItemsException extends Exception {
        public GrnWithItemsException(String grnId) {
            super("Cannot delete GRN " + grnId + ": it contains associated items");
        }
    }

    public static class InvalidStateTransitionException extends Exception {
        public InvalidStateTransitionException(Enum<?> from, Enum<?> to) {
            super("Invalid state transition: " + (from != null ? from.name() : "null") +
                    " → " + (to != null ? to.name() : "null"));
        }
    }

    public static class DuplicateGrnCodeException extends Exception {
        public DuplicateGrnCodeException(String code) {
            super("A GRN with code " + code + " already exists");
        }
    }

    public static class InvalidQuantityException extends Exception {
        public InvalidQuantityException(String message) {
            super(message);
        }
    }

    public static class QuantityMismatchException extends Exception {
        public QuantityMismatchException(String message) {
            super(message);
        }
    }

    public static class OverReceivedQuantityException extends Exception {
        public OverReceivedQuantityException(String message) {
            super(message);
        }
    }
}
