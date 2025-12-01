package com.relatech.warehouse_management_system.goodsIn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "grn_code", nullable = false, unique = true)
    private String code;

    @Column(name = "supplier", nullable = false, length = 100)
    private String supplier;

    @Column(name = "receiving_date", nullable = false)
    private LocalDate receivingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private State state;

    @OneToMany
    @JoinColumn(name = "grn_id")
    @JsonIgnore
    private List<GrnItem> items;

    public void addItem(GrnItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        items.add(item);
        item.setGrn(this);
    }

    public void addItems(List<GrnItem> itemList) {
        for (GrnItem item : itemList) {
            addItem(item); // this will set grn for each item
        }
    }

    public void removeItem(GrnItem item) {
        items.remove(item);
    }
}
