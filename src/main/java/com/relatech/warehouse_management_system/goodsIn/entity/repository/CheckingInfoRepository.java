package com.relatech.warehouse_management_system.goodsIn.entity.repository;

import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckingInfoRepository extends JpaRepository<CheckingInfo, Long> {
    List<CheckingInfo> findByGrnItemId(Long grnItemId);
    Optional<CheckingInfo> findByCode(@NotBlank(message = "The code cannot be empty or null.") String code);
}
