package com.relatech.warehouse_management_system.goodsIn.entity.repository;

import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrnItemRepository extends JpaRepository<GrnItem, Long> {
    @Query("SELECT gi FROM GrnItem gi LEFT JOIN FETCH gi.checkingInfoList WHERE gi.id = :id")
    Optional<GrnItem> findByIdWithCheckingInfos(@Param("id") Long id);

    List<GrnItem> findByGrnId(Long grnId);
    Optional<GrnItem> findByCode(@NotBlank(message = "The code cannot be empty or null.") String code);
}
