package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDto;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnWithItemsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GrnService {

    GrnDto createGRN(GrnDto grnDTO);

    GrnDto getGRNById(Long id) throws GrnNotFoundException;
    GrnDto getGRNByCode(String code) throws GrnNotFoundException;
    List<GrnDto> getAllGRNs();
    Page<GrnDto> getAllGRNsPaged(Pageable pageable);
    GrnDto updateGRN(Long id, GrnDto grnDTO) throws GrnNotFoundException;
    void deleteById(Long id) throws GrnNotFoundException, GrnWithItemsException;
    List<GrnDto> searchGrns(String term);
}
