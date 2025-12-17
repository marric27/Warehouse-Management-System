package com.relatech.warehouse_management_system.picking.entity.service;

import com.relatech.warehouse_management_system.picking.entity.PickingInfo;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoDto;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoMapper;
import com.relatech.warehouse_management_system.picking.entity.PickingInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickingInfoService {
    private final PickingInfoRepository pickingInfoRepository;

    public PickingInfoDto create(PickingInfoDto pickingInfoDto) {
        PickingInfo pickingInfo = PickingInfoMapper.toEntity(pickingInfoDto);
        return PickingInfoMapper.toDto(pickingInfoRepository.save(pickingInfo));
    }



}
