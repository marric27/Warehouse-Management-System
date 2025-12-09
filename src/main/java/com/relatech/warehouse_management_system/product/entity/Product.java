package com.relatech.warehouse_management_system.product.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 26)
    private String code;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            code = UlidCreator.getUlid().toString();
        }
    }

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
}
