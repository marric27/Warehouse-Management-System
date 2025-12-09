package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.CheckingInfoMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckingInfoServiceImpl implements CheckingInfoService {

    private final CheckingInfoRepository checkingInfoRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public CheckingInfoDto create(CheckingInfoDto dto) {
        CheckingInfo saved = checkingInfoRepository.save(CheckingInfoMapper.toEntity(dto));
        return CheckingInfoMapper.toDto(saved);
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, Exception.class}, propagation = Propagation.REQUIRES_NEW)
    public CheckingInfoDto update(Long id, CheckingInfoDto dto) throws ResourceNotFoundException {

        CheckingInfo existing = checkingInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", id));

        existing.setBatchNumber(dto.getBatchNumber());
        existing.setExpirationDate(dto.getExpirationDate());
        existing.setQuantity(dto.getQuantity());
        existing.setState(dto.getState());
        existing.setStockUnitId(dto.getStockUnitId());

        CheckingInfo saved = checkingInfoRepository.save(existing);
        return CheckingInfoMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckingInfoDto getById(Long id) throws ResourceNotFoundException {
        return checkingInfoRepository.findById(id)
                .map(CheckingInfoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", id));
    }

    @Override
    @Transactional(readOnly = true)
    public CheckingInfoDto getByCode(String code) throws ResourceNotFoundException {
        return checkingInfoRepository.findByCode(code)
                .map(CheckingInfoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckingInfoDto> getAll() {
        return checkingInfoRepository.findAll()
                .stream()
                .map(CheckingInfoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class}, propagation = Propagation.REQUIRES_NEW)
    public void delete(Long id) throws ResourceNotFoundException {
        if (!checkingInfoRepository.existsById(id)) {
                throw new ResourceNotFoundException("CheckingInfo", id);
        }
        checkingInfoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckingInfoDto> getAllById(List<Long> checkingInfoIds) {
        return CheckingInfoMapper.toDtoList(checkingInfoRepository.findAllById(checkingInfoIds));
    }

    @Override
    @Transactional(readOnly = true)
    public CheckingInfoDto getByStockUnitId(Long suId) throws ResourceNotFoundException {
        CheckingInfo entity = checkingInfoRepository.findByStockUnitId(suId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", suId));
        return CheckingInfoMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckingInfoDto> getByGrnItemId(Long id) {
        return CheckingInfoMapper.toDtoList(checkingInfoRepository.findByGrnItemId(id));
    }
}