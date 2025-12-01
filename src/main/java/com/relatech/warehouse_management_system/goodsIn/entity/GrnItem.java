package com.relatech.warehouse_management_system.goodsIn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.common.util.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grn_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GrnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "expected_qty", nullable = false)
    private int expectedQty;

    @Column(name = "received_qty")
    private int receivedQty;

    @Column(name = "compliant_qty")
    private int compliantQty;

    @Column(name = "not_compliant_qty")
    private int notCompliantQty;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Column(name = "notes", length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "grn_id")
    @JsonIgnore
    private GRN grn;

    @OneToMany(mappedBy = "grnItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CheckingInfo> checkingInfoList = new ArrayList<>();
}
