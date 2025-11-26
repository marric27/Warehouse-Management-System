package com.relatech.warehouse_management_system.grnItem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.util.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grn_item")
public class GrnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grn_item_code", nullable = false, unique = true)
    private String code;

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

    @ManyToOne
    @JoinColumn(name = "grn_id")
    private GRN grn;

    @OneToMany(mappedBy = "grnItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CheckingInfo> checkingInfoList;

    public void addCInfo(CheckingInfo ci) {
        if (this.checkingInfoList == null) {
            this.checkingInfoList = new ArrayList<>();
        }
        checkingInfoList.add(ci);
        ci.setGrnItem(this);
    }

    public void addCInfos(List<CheckingInfo> checkingInfoList) {
        for (CheckingInfo ci : checkingInfoList) {
            addCInfo(ci); // this will set grn for each item
        }
    }

    public void removeItem(CheckingInfo ci) {
        checkingInfoList.remove(ci);
    }


}
