package com.relatech.warehouse_management_system.checkingInfo.service;

import com.relatech.warehouse_management_system.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.checkingInfo.mapper.CheckingInfoMapper;
import com.relatech.warehouse_management_system.checkingInfo.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.event.CheckingInfoUpdatedEvent;
import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.grnItem.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckingInfoServiceImpl implements CheckingInfoService {

    private final CheckingInfoRepository checkingInfoRepository;
    private final StockUnitRepository stockUnitRepository;
    private final GrnItemRepository grnItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CheckingInfoDto create(CheckingInfoDto dto) {
        CheckingInfo saved = checkingInfoRepository.save(CheckingInfoMapper.toEntity(dto));
        return CheckingInfoMapper.toDto(saved);
    }

    @Override
    @Transactional
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
    public CheckingInfoDto getById(Long id) throws ResourceNotFoundException {
        return checkingInfoRepository.findById(id)
                .map(CheckingInfoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", id));
    }

    @Override
    public List<CheckingInfoDto> getAll() {
        return checkingInfoRepository.findAll()
                .stream()
                .map(CheckingInfoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) throws ResourceNotFoundException {
        if (!checkingInfoRepository.existsById(id)) {
                throw new ResourceNotFoundException("CheckingInfo", id);
        }
        checkingInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CheckingInfo setStockUnit(Long checkingInfoId, Long stockUnitId) throws ResourceNotFoundException {
        CheckingInfo ci = checkingInfoRepository.findById(checkingInfoId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", checkingInfoId));
        StockUnit stockUnit = stockUnitRepository.findById(stockUnitId)
                        .orElseThrow(() -> new ResourceNotFoundException("Stock Unit", stockUnitId));

        ci.setStockUnit(stockUnitId);
        return checkingInfoRepository.save(ci);
    }

    @Override
    @Transactional
    public CheckingInfo updateCheckingInfoState(Long checkingInfoId, State newState) throws ResourceNotFoundException {
        CheckingInfo ci = checkingInfoRepository.findById(checkingInfoId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", checkingInfoId));

        ci.setState(newState);
        checkingInfoRepository.save(ci);

        eventPublisher.publishEvent(new CheckingInfoUpdatedEvent(ci));
        log.info("Publish new event CheckingInfoUpdatedEvent");

        return ci;
    }
}
