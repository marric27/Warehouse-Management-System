package com.relatech.warehouse_management_system.checkingInfo.repository;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingInfoRepository extends JpaRepository<CheckingInfo, Long> {
}
