package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.warehouse.entity.SlotDto;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import com.relatech.warehouse_management_system.warehouse.entity.SlotMapper;
import com.relatech.warehouse_management_system.warehouse.entity.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogisticServiceImpl implements LogisticService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SlotMapper slotMapper;

    @Override
    @Transactional
    public SlotDto assignProductToSlot(Long slotId, Long productId) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        slot.addProduct(product);
        return slotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    @Transactional
    public SlotDto removeProductFromSlot(Long id) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
        slot.setProd(null);
        return slotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSlotContainProduct(Long slotId, Long productId) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        return slot.canContain(product);
    }
}
