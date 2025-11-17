package com.relatech.warehouse_management_system.slot.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(SlotMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SlotDTO getSlotById(Long id) throws ResourceNotFoundException {
        return slotRepository.findById(id)
                .map(SlotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), id));
    }

    @Override
    @Transactional
    public SlotDTO createSlot(SlotDTO slotDTO) {
        Slot slot = SlotMapper.toEntity(slotDTO);
        Slot savedSlot = slotRepository.save(slot);
        return SlotMapper.toDto(savedSlot);
    }

    @Override
    @Transactional
    public SlotDTO updateSlot(Long id, SlotDTO slotDTO) throws Exception {
        Slot existingSlot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), id));

        existingSlot.setCode(slotDTO.getCode());
        existingSlot.setCapacity(slotDTO.getCapacity());

        if (existingSlot.getProd() == null)
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        else if (existingSlot.getProd().getCategory().equals(slotDTO.getAllowedCategory())) {
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        } else throw new Exception("Cant update slot product category cause contains a product");

        Slot updatedSlot = slotRepository.save(existingSlot);
        return SlotMapper.toDto(updatedSlot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.getClass().getName(), id));

        if (slot.getProd() != null) {
            throw new IllegalStateException("Cannot delete slot because it contains a product");
        }

        slotRepository.deleteById(id);
    }

}
