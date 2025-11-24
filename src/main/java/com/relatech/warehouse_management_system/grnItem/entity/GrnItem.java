package com.relatech.warehouse_management_system.grnItem.entity;

import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.util.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grn_item")
public class GrnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GRN grn;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "expected_qty", nullable = false)
    private int expectedQty;

    @Column(name = "received_qty", nullable = false)
    private int receivedQty;

    @Column(name = "compliant_qty", nullable = false)
    private int compliantQty;

    @Column(name = "not_compliant_qty", nullable = false)
    private int notCompliantQty;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private State state;

    @OneToMany
    private List<CheckingInfo> checkingInfoList;
}
