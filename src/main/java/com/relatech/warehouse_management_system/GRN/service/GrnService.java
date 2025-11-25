package com.relatech.warehouse_management_system.GRN.service;

import com.relatech.warehouse_management_system.GRN.dto.GrnDTO;
import com.relatech.warehouse_management_system.exception.*;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface GrnService {
    GrnDTO createGRN(GrnDTO grnDTO) throws DuplicateResourceException;
    GrnDTO getGRNById(Long id) throws ResourceNotFoundException;
    List<GrnDTO> getAllGRNs();
    Page<GrnDTO> getAllGRNsPaged(Pageable pageable);
    GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException, GrnWithItemsException;
    Page<GrnItemDto> findItemsByGrnId(Long grnId, Pageable pageable) throws ResourceNotFoundException;
    GrnDTO updateStatus(Long grnId, String status) throws ResourceNotFoundException;
    List<GrnDTO> searchGrns(String term);

    GrnDTO addItemsToGrn(Long grnId, List<Long> itemIds) throws ResourceNotFoundException;
}
