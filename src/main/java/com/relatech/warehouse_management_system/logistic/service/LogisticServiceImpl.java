package com.relatech.warehouse_management_system.logistic.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.Slot;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.SlotMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.SlotRepository;
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
    public SlotDTO assignProductToSlot(Long slotId, Long productId) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        slot.addProduct(product);
        return slotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    @Transactional
    public SlotDTO removeProductFromSlot(Long id) throws ResourceNotFoundException {
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
