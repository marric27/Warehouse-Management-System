package com.relatech.warehouse_management_system.GRN.entity;

import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import com.relatech.warehouse_management_system.util.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "grn")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GRN {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "supplier", nullable = false, length = 100)
    private String supplier;

    @Column(name = "receiving_date", nullable = false)
    private LocalDate receivingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private State state;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GrnItem> items;
}
