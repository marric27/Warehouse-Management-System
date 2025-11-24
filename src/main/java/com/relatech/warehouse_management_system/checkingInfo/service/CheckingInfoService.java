package com.relatech.warehouse_management_system.checkingInfo.service;

import com.relatech.warehouse_management_system.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;

import java.util.List;

public interface CheckingInfoService {

    CheckingInfoDto create(CheckingInfoDto dto);

    CheckingInfoDto update(Long id, CheckingInfoDto dto) throws ResourceNotFoundException;

    CheckingInfoDto getById(Long id) throws ResourceNotFoundException;

    List<CheckingInfoDto> getAll();

    void delete(Long id) throws ResourceNotFoundException;
}
