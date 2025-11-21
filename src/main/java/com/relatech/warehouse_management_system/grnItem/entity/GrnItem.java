package com.relatech.warehouse_management_system.grnItem.entity;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.util.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private int expectedQty;

    @Column(nullable = false)
    private int receivedQty;

    @Column(nullable = false)
    private int compliantQty;

    @Column(nullable = false)
    private int notCompliantQty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @OneToMany
    private List<CheckingInfo> checkingInfoList;
}
