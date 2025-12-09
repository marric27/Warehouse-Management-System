package com.relatech.warehouse_management_system.goodsIn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CheckingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkinginfo_code", nullable = false, unique = true, length = 13)
    private String code;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "CI-" + ulid.substring(0, 10).toUpperCase();
        }
    }

    @Column(name = "batch_number", nullable = false)
    private String batchNumber; // lotto

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate; // data di scadenza

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // quantità prodotto

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private State state;

    @ManyToOne
    @JoinColumn(name = "grn_item_id")
    @JsonIgnore
    private GrnItem grnItem;

    @Column(name = "stock_unit_id", unique = true)
    private Long stockUnitId;
}