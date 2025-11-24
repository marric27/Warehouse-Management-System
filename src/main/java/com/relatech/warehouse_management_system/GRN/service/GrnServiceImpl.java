package com.relatech.warehouse_management_system.GRN.service;

import com.relatech.warehouse_management_system.GRN.dto.GrnDTO;
import com.relatech.warehouse_management_system.GRN.entity.GRN;
import com.relatech.warehouse_management_system.GRN.mapper.GrnMapper;
import com.relatech.warehouse_management_system.GRN.repository.GrnRepository;
import com.relatech.warehouse_management_system.exception.*;
import com.relatech.warehouse_management_system.grnItem.dto.GrnItemDto;
import com.relatech.warehouse_management_system.grnItem.mapper.GrnItemMapper;
import com.relatech.warehouse_management_system.util.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @Override
    @Transactional(rollbackFor = DuplicateResourceException.class, propagation = Propagation.REQUIRED)
    public GrnDTO createGRN(GrnDTO grnDTO) throws DuplicateResourceException {
        log.debug("Creating new GRN with ID: {}", grnDTO.getId());

        if (grnRepository.existsById(grnDTO.getId())) {
            log.warn("GRN with ID {} already exists", grnDTO.getId());
            throw new DuplicateResourceException("GRN", "id", grnDTO.getId());
        }

        GRN entity = grnMapper.toEntity(grnDTO);
        GRN saved = grnRepository.save(entity);
        log.info("GRN created successfully with ID: {}", saved.getId());
        return grnMapper.toDto(saved);
    }

    @Override
    public GrnDTO getGRNById(String id) throws ResourceNotFoundException {
        log.debug("Fetching GRN with ID: {}", id);
        GRN entity = grnRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", id);
                    return new ResourceNotFoundException("GRN", id);
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
    public GrnDTO updateGRN(String id, GrnDTO grnDTO) throws ResourceNotFoundException {
        log.debug("Updating GRN with ID: {}", id);

        GRN existing = grnRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", id);
                    return new ResourceNotFoundException("GRN", id);
                });

        existing.setSupplier(grnDTO.getSupplier());
        existing.setReceivingDate(grnDTO.getReceivingDate());

        GRN saved = grnRepository.save(existing);
        log.info("GRN updated successfully with ID: {}", saved.getId());
        return grnMapper.toDto(saved);
    }

    @Override
    @Transactional(rollbackFor = {ResourceNotFoundException.class, GrnWithItemsException.class}, propagation = Propagation.REQUIRES_NEW)
    public void deleteById(String id) throws ResourceNotFoundException, GrnWithItemsException {
        log.debug("Deleting GRN with ID: {}", id);

        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", id);
                    return new ResourceNotFoundException("GRN", id);
                });

        if (grn.getItems() != null && !grn.getItems().isEmpty()) {
            log.warn("Cannot delete GRN {} as it has associated items", id);
            throw new GrnWithItemsException(id);
        }

        grnRepository.delete(grn);
        log.info("GRN deleted successfully with ID: {}", id);
    }

    @Override
    public Page<GrnItemDto> findItemsByGrnId(String grnId, Pageable pageable) throws ResourceNotFoundException {
        log.debug("Fetching items for GRN: {}", grnId);

        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", grnId);
                    return new ResourceNotFoundException("GRN", grnId);
                });

        List<GrnItemDto> items = (grn.getItems() != null)
                ? grn.getItems().stream().map(GrnItemMapper::toDto).toList()
                : List.of();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), items.size());
        List<GrnItemDto> paginatedItems = items.subList(start, end);

        return new PageImpl<>(paginatedItems, pageable, items.size());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GrnDTO updateStatus(String grnId, String status) throws ResourceNotFoundException {
        log.debug("Updating status for GRN {} to {}", grnId, status);

        GRN entity = grnRepository.findById(grnId)
                .orElseThrow(() -> {
                    log.warn("GRN not found with ID: {}", grnId);
                    return new ResourceNotFoundException("GRN", grnId);
                });

        try {
            entity.setState(State.valueOf(status));
            GRN saved = grnRepository.save(entity);
            log.info("Status updated for GRN {} to {}", grnId, status);
            return grnMapper.toDto(saved);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status value: {}", status);
            throw new IllegalArgumentException("Invalid status: " + status, e);
        }
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
}