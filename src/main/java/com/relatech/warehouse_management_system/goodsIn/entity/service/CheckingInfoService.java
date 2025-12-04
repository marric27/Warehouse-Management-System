package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.common.util.State;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

public interface CheckingInfoService {

    CheckingInfoDto create(CheckingInfoDto dto);

    CheckingInfoDto update(Long id, CheckingInfoDto dto) throws ResourceNotFoundException;

    CheckingInfoDto getById(Long id) throws ResourceNotFoundException;

    CheckingInfoDto getByCode(String code) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAll();

    void delete(Long id) throws ResourceNotFoundException;

    CheckingInfo setStockUnit(Long checkingInfoId, Long stockUnitId) throws Exception;

    CheckingInfo updateCheckingInfoState(Long checkingInfoId, State newState) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAllById(List<Long> checkingInfoIds);

    CheckingInfoDto getByStockUnitId(Long suId);

    List<CheckingInfoDto> getByGrnItemId(Long id);
}
