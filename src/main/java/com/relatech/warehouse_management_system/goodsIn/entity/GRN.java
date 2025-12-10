package com.relatech.warehouse_management_system.goodsIn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grn")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GRN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grn_code", nullable = false, unique = true, length = 14)
    private String code;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "GRN-" + ulid.substring(0, 10).toUpperCase();
        }
    }

    @Column(name = "supplier", nullable = false, length = 100)
    private String supplier;

    @Column(name = "receiving_date", nullable = false)
    private LocalDate receivingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private State state;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<GrnItem> items = new ArrayList<>();


    // ---------- UTILITIES ----------
    public void addItem(GrnItem item) {
        if (items == null)
            items = new ArrayList<>();
        items.add(item);
        item.setGrn(this);
    }

    public void addItems(List<GrnItem> itemList) {
        for (GrnItem item : itemList) {
            addItem(item);
        }
    }

    public void removeItem(GrnItem item) {
        items.remove(item);
        item.setGrn(null);
    }

    public void setItems(List<GrnItem> list) {
        this.items = (list != null) ? new ArrayList<>(list) : new ArrayList<>();
    }
}
