package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogisticServiceImpl implements LogisticService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public SlotDTO assignProductToSlot(Long slotId, Long productId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Can't assign product to slot. Slot not found with id: " + slotId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Can't assign product to slot. Product not found with id: " + productId));

        slot.addProduct(product);
        return SlotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    public SlotDTO removeProductFromSlot(Long id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't remove product from slot. Slot not found with id: " + id));
        slot.setProd(null);
        return SlotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    public boolean canSlotContainProduct(Long slotId, Long productId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + slotId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        return slot.canContain(product);
    }
}
