package com.relatech.warehouse_management_system.outbound.entity;

import com.github.f4b6a3.ulid.UlidCreator;
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

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "PKL-" + ulid.substring(0, 10).toUpperCase();
        }
    }

    @Column(nullable = false)
    private String customerCode;

    @Builder.Default
    @OneToMany(
            mappedBy = "pickList",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PickListItem> pickListItemList = new ArrayList<>();
}