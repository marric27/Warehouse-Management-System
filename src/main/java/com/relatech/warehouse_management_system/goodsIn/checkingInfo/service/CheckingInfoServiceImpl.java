package com.relatech.warehouse_management_system.goodsIn.checkingInfo.service;

import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnRepository;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.dto.CheckingInfoDto;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.mapper.CheckingInfoMapper;
import com.relatech.warehouse_management_system.goodsIn.checkingInfo.repository.CheckingInfoRepository;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnItemRepository;
import com.relatech.warehouse_management_system.common.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final GrnItemRepository grnItemRepository;
    private final GrnRepository grnRepository;

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
    public CheckingInfo setStockUnit(Long checkingInfoId, Long stockUnitId) throws Exception {
        CheckingInfo ci = checkingInfoRepository.findById(checkingInfoId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", checkingInfoId));

        if (ci.getGrnItem() != null) {
            ci.setStockUnit(stockUnitId);
            updateCheckingInfoState(checkingInfoId, State.PUTAWAY);
            checkAllCIPutawayForGi(ci.getGrnItem());
        }
        else
            throw new Exception("cant assign stock unit to checkinfo cause checkinfo is not assigned to any grnitem");

        return ci;
    }

    private void checkAllCIPutawayForGi(GrnItem gi) {
        boolean allPutaway = gi.getCheckingInfoList()
                .stream()
                .allMatch(info -> info.getState() == State.PUTAWAY);

        if (gi.getState() == State.CHECKED && allPutaway) {
            gi.setState(State.PUTAWAY);
            GRN grn = gi.getGrn();
            grn.setState(State.CLOSED);

            grnItemRepository.save(gi);
            grnRepository.save(grn);
            log.info("Updated GrnItem {} to state {}", gi.getId(), State.PUTAWAY);
            log.info("Updated Grn {} to state {}", grn.getId(), State.CLOSED);
        }
    }

    //TODO potrei anche non esporlo ma usarlo solo internamente
    @Override
    @Transactional
    public CheckingInfo updateCheckingInfoState(Long checkingInfoId, State newState) throws ResourceNotFoundException {
        log.info("Updating checkinginfo {} to state {}", checkingInfoId, newState);
        CheckingInfo ci = checkingInfoRepository.findById(checkingInfoId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckingInfo", checkingInfoId));

        ci.setState(newState);
        checkingInfoRepository.save(ci);
        log.info("Updated checkinginfo {} to state {}", checkingInfoId, newState);
        return ci;
    }

    @Override
    public List<CheckingInfo> getAllById(List<Long> checkingInfoIds) {
        return checkingInfoRepository.findAllById(checkingInfoIds);
    }
}