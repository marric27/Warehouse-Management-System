package com.relatech.warehouse_management_system.goodsIn.checkingInfo.repository;

import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckingInfoRepository extends JpaRepository<CheckingInfo, Long> {
    List<CheckingInfo> findByGrnItemId(Long grnItemId);
}
