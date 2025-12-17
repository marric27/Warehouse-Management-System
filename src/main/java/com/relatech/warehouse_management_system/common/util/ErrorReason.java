package com.relatech.warehouse_management_system.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorReason {
    MISSING_QTY("Quantità insufficiente"),
    INVALID_PRICE("Prezzo non valido"),
    OUT_OF_STOCK("Prodotto non disponibile"),
    UNAUTHORIZED("Utente non autorizzato"),
    NOT_FOUND("Risorsa non trovata");

    private final String message;
}
