package com.relatech.warehouse_management_system.goodsIn.checkingInfo.service;

import com.relatech.warehouse_management_system.goodsIn.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CheckingInfoService {

    CheckingInfoDto create(CheckingInfoDto dto);

    CheckingInfoDto update(Long id, CheckingInfoDto dto) throws ResourceNotFoundException;

    CheckingInfoDto getById(Long id) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAll();

    void delete(Long id) throws ResourceNotFoundException;

    @Transactional
    CheckingInfo setStockUnit(Long checkingInfoId, Long stockUnitId) throws Exception;

    @Transactional
    CheckingInfo updateCheckingInfoState(Long checkingInfoId, State newState) throws ResourceNotFoundException;
}
