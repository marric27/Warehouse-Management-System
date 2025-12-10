package com.relatech.warehouse_management_system.outbound.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PickList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String customerCode;

    @Column(nullable = false)
    private String salesOrderCode;

    @OneToMany(
            mappedBy = "pickList",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PickListItem> pickListItemList = new ArrayList<>();
}