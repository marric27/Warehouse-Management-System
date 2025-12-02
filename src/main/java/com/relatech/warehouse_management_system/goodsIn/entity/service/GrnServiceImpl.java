package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.goodsIn.dto.GrnDTO;
import com.relatech.warehouse_management_system.goodsIn.dto.GrnItemDto;
import com.relatech.warehouse_management_system.goodsIn.entity.GRN;
import com.relatech.warehouse_management_system.goodsIn.entity.GrnItem;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.GrnMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.GrnRepository;
import com.relatech.warehouse_management_system.goodsIn.exception.GrnExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GrnServiceImpl implements GrnService {

    private final GrnRepository grnRepository;
    private final GrnMapper grnMapper;
    private final GrnItemMapper grnItemMapper;

    @Override
    @Transactional(rollbackFor = GrnExceptions.DuplicateGrnCodeException.class, propagation = Propagation.REQUIRED)
    public GrnDTO createGRN(GrnDTO grnDTO) {
        log.debug("Creating new GRN with ID: {}", grnDTO.getId());

        GRN entity = grnMapper.toEntity(grnDTO);
        GRN saved = grnRepository.save(entity);
        log.info("GRN created successfully with ID: {}", saved.getId());
        return grnMapper.toDto(saved);
    }

    @Override
    public GrnDTO getGRNById(Long id) throws GrnExceptions.GrnNotFoundException {
        log.debug("Fetching GRN with ID: {}", id);
        GRN entity = grnRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", id);
                    return new GrnExceptions.GrnNotFoundException(id);
                });
        return grnMapper.toDto(entity);
    }

    @Override
    public GrnDTO getGRNByCode(String code) throws GrnExceptions.GrnNotFoundException {
        log.debug("Fetching GRN with code: {}", code);
        GRN entity = grnRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("GRN not found with code: {}", code);
                    return new GrnExceptions.GrnNotFoundException(code);
                });
        return grnMapper.toDto(entity);
    }

    @Override
    public List<GrnDTO> getAllGRNs() {
        log.debug("Fetching all GRNs");
        return grnRepository.findAll().stream()
                .map(grnMapper::toDto)
                .toList();
    }

    @Override
    public Page<GrnDTO> getAllGRNsPaged(Pageable pageable) {
        log.debug("Fetching paginated GRNs: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<GRN> grnPage = grnRepository.findAll(pageable);
        return grnPage.map(grnMapper::toDto);
    }

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRED)
    public GrnDTO updateGRN(Long id, GrnDTO grnDTO) throws GrnExceptions.GrnNotFoundException {
        GRN existing = grnRepository.findById(id)
                .orElseThrow(() -> new GrnExceptions.GrnNotFoundException(id));

        if (grnDTO.getSupplier() != null)
            existing.setSupplier(grnDTO.getSupplier());
        if (grnDTO.getReceivingDate() != null)
            existing.setReceivingDate(grnDTO.getReceivingDate());

        GRN saved = grnRepository.save(existing);
        return grnMapper.toDto(saved);
    }

    @Override
    @Transactional(rollbackFor = {GrnExceptions.GrnNotFoundException.class, GrnExceptions.GrnWithItemsException.class},
            propagation = Propagation.REQUIRES_NEW)
    public void deleteById(Long id) throws GrnExceptions.GrnNotFoundException, GrnExceptions.GrnWithItemsException {
        log.debug("Deleting GRN with ID: {}", id);

        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", id);
                    return new GrnExceptions.GrnNotFoundException(id);
                });

        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            log.warn("Cannot delete GRN {} as it has associated items", id);
            throw new GrnExceptions.GrnWithItemsException("" + id);
        }

        grnRepository.delete(grn);
        log.info("GRN deleted successfully with ID: {}", id);
    }

    @Override
    public List<GrnDTO> searchGrns(String term) {
        log.debug("Searching GRNs with term: {}", term);

        if (term == null || term.trim().isEmpty()) {
            return getAllGRNs();
        }

        return grnRepository.searchByTerm(term).stream()
                .map(grnMapper::toDto)
                .toList();
    }

    @Override
    public GrnItem addItemToGrn(Long grnId, GrnItemDto dto) throws GrnExceptions.GrnItemNotFoundException {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GrnExceptions.GrnItemNotFoundException(dto.getId()));

        GrnItem item = grnItemMapper.toEntity(dto);

        grn.addItem(item);
        grnRepository.save(grn);

        return item;
    }
}
