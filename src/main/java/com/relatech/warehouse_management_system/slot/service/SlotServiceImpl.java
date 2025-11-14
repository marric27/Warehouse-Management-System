package com.relatech.warehouse_management_system.slot.service;

import com.relatech.warehouse_management_system.exception.EntityNotFoundException;
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
@Transactional
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(SlotMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SlotDTO getSlotById(Long id) throws EntityNotFoundException {
        return slotRepository.findById(id)
                .map(SlotMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Slot with id " + id + " not found"));
    }

    @Override
    public SlotDTO createSlot(SlotDTO slotDTO) {
        Slot slot = SlotMapper.toEntity(slotDTO);
        Slot savedSlot = slotRepository.save(slot);
        return SlotMapper.toDto(savedSlot);
    }

    @Override
    public SlotDTO updateSlot(Long id, SlotDTO slotDTO) throws Exception {
        Slot existingSlot = slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + id));

        existingSlot.setCode(slotDTO.getCode());
        existingSlot.setCapacity(slotDTO.getCapacity());

        if (existingSlot.getProd() == null)
            existingSlot.setProductCategory(slotDTO.getProductCategory());
        else if (existingSlot.getProd().getProductCategory().equals(slotDTO.getProductCategory())) {
            existingSlot.setProductCategory(slotDTO.getProductCategory());
        } else throw new Exception("Cant update slot product category cause contains a product");

        Slot updatedSlot = slotRepository.save(existingSlot);
        return SlotMapper.toDto(updatedSlot);
    }

    @Override
    public void deleteSlot(Long id) throws EntityNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Slot with id " + id + " not found. Delete failed"));

        if (slot.getProd() != null) {
            throw new IllegalStateException("Cannot delete slot because it contains a product");
        }

        slotRepository.deleteById(id);
    }

}
