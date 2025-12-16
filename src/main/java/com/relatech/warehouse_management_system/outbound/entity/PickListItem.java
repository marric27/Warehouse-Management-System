package com.relatech.warehouse_management_system.outbound.entity;

import com.relatech.warehouse_management_system.common.util.PickListItemState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pick_list_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PickListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickListItemState state;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    private Integer pickingSequence;

    @Column(nullable = false)
    private String slotCode;

    @Column(nullable = false)
    private String salesOrderCode;

    @Column(nullable = false)
    private Integer salesOrderLineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

}