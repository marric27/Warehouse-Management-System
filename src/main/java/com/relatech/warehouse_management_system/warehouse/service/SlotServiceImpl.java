package com.relatech.warehouse_management_system.warehouse.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.entity.SlotMapper;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private StockUnitRepository stockUnitRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SlotMapper slotMapper;

    @Autowired
    private StockUnitMapper stockUnitMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SlotDto> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(slotMapper::toDto).toList();
    }

    @Override
    public Page<SlotDto> getAllSlotsPaged(Pageable pageable) {
        Page<Slot> slotPage = slotRepository.findAll(pageable);
        return slotPage.map(slotMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SlotDto getSlotById(Long id) throws ResourceNotFoundException {
        return slotRepository.findById(id)
                .map(slotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
    }

    @Override
    @Transactional
    public SlotDto createSlot(SlotDto slotDTO) {
        Slot slot = slotMapper.toEntity(slotDTO);
        Slot savedSlot = slotRepository.save(slot);
        return slotMapper.toDto(savedSlot);
    }

    @Override
    @Transactional
    public SlotDto updateSlot(Long id, SlotDto slotDTO) throws ResourceNotFoundException, UpdateEntityException {
        Slot existingSlot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        existingSlot.setCapacity(slotDTO.getCapacity());

        if (existingSlot.getProd() == null)
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        else if (existingSlot.getProd().getCategory().equals(slotDTO.getAllowedCategory())) {
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        } else throw new UpdateEntityException(id);

        if (slotDTO.getStockUnits() != null) {
            List<StockUnit> existingUnits = existingSlot.getStockUnits();
            for (StockUnitDto stockUnitDTO : slotDTO.getStockUnits()) {
                if (stockUnitDTO.getId() != null) {
                    // StockUnit esistente → prendo dal DB
                    StockUnit stockUnit = stockUnitRepository.findById(stockUnitDTO.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitDTO.getId()));

                    // Controllo duplicati
                    boolean alreadyLinked = existingUnits.stream()
                            .anyMatch(u -> u.getId().equals(stockUnit.getId()));

                    if (!alreadyLinked) {
                        stockUnit.setSlot(existingSlot);
                        existingUnits.add(stockUnit);
                    }
                } else {
                    // StockUnit nuova
                    StockUnit stockUnit = stockUnitMapper.toEntity(stockUnitDTO);
                    stockUnit.setSlot(existingSlot);

                    // Evito duplicazioni per oggetti nuovi
                    if (!existingUnits.contains(stockUnit)) {
                        existingUnits.add(stockUnit);
                    }
                }
            }
        }
        Slot updatedSlot = slotRepository.save(existingSlot);
        return slotMapper.toDto(updatedSlot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        if (slot.getProd() != null) {
            throw new IllegalStateException("Cannot delete slot because it contains a product");
        }
        if (slot.getStockUnits() != null && !slot.getStockUnits().isEmpty()) {
            throw new IllegalStateException("Cannot delete slot because it contains stock units");
        }

        slotRepository.deleteById(id);
    }

    @Override
    public Optional<SlotDto> getSlotContainingProduct(String productCode, int requiredQuantity) {
        List<Slot> slotdto = slotRepository.findDistinctByStockUnitsProductCode(productCode);
        return slotRepository.findDistinctByStockUnitsProductCode(productCode).stream()
                //.filter(slot -> slot.getAvailableQuantity() >= requiredQuantity)
                .findFirst()
                .map(slotMapper::toDto);
    }

    @Override
    public SlotDto getSlotByCode(String slotCode) throws ResourceNotFoundException {
        return slotRepository.findByCode(slotCode)
                .map(slotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotCode));
    }

}
