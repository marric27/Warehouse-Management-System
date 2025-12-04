package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;

import java.util.List;

public interface CheckingInfoService {

    CheckingInfoDto create(CheckingInfoDto dto);

    CheckingInfoDto update(Long id, CheckingInfoDto dto) throws ResourceNotFoundException;

    CheckingInfoDto getById(Long id) throws ResourceNotFoundException;

    CheckingInfoDto getByCode(String code) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAll();

    void delete(Long id) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAllById(List<Long> checkingInfoIds);

    CheckingInfoDto getByStockUnitId(Long suId) throws ResourceNotFoundException;

    List<CheckingInfoDto> getByGrnItemId(Long id);
}
