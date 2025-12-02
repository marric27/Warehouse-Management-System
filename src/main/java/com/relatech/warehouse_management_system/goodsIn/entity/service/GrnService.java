package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GrnService {

    GrnDTO createGRN(GrnDTO grnDTO) throws GrnExceptions.DuplicateGrnCodeException;

    GrnDTO getGRNById(Long id) throws GrnExceptions.GrnNotFoundException;
    GrnDTO getGRNByCode(String code) throws GrnExceptions.GrnNotFoundException;
    List<GrnDTO> getAllGRNs();
    Page<GrnDTO> getAllGRNsPaged(Pageable pageable);
    GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws GrnExceptions.GrnNotFoundException;
    void deleteById(Long id) throws GrnExceptions.GrnNotFoundException, GrnExceptions.GrnWithItemsException;
    List<GrnDTO> searchGrns(String term);

    GrnItem addItemToGrn(Long grnId, GrnItemDto dto) throws GrnExceptions.GrnItemNotFoundException;
}
