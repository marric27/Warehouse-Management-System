package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnWithItemsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GrnService {

    GrnDTO createGRN(GrnDTO grnDTO);

    GrnDTO getGRNById(Long id) throws GrnNotFoundException;
    GrnDTO getGRNByCode(String code) throws GrnNotFoundException;
    List<GrnDTO> getAllGRNs();
    Page<GrnDTO> getAllGRNsPaged(Pageable pageable);
    GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws GrnNotFoundException;
    void deleteById(Long id) throws GrnNotFoundException, GrnWithItemsException;
    List<GrnDTO> searchGrns(String term);
}
