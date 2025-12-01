package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.common.exception.GrnWithItemsException;
import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GrnService {
    GrnDTO getGRNById(Long id) throws ResourceNotFoundException;
    GrnDTO getGRNByCode(String code) throws ResourceNotFoundException;
    List<GrnDTO> getAllGRNs();
    Page<GrnDTO> getAllGRNsPaged(Pageable pageable);
    GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException, GrnWithItemsException;
    List<GrnDTO> searchGrns(String term);
}
