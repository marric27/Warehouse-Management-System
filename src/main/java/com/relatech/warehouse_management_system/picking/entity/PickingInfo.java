package com.relatech.warehouse_management_system.picking.entity;

import com.relatech.warehouse_management_system.outbound.entity.PickListItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "picking_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PickingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false)
    private String stockUnitCode;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pick_list_item_id", nullable = false)
    private PickListItem pickListItem;
}
