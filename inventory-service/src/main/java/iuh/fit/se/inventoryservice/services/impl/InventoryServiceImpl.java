package iuh.fit.se.inventoryservice.services.impl;

import iuh.fit.se.inventoryservice.dtos.InventoryRequest;
import iuh.fit.se.inventoryservice.dtos.InventoryResponse;
import iuh.fit.se.inventoryservice.dtos.OrderMessage;
import iuh.fit.se.inventoryservice.entities.Inventory;
import iuh.fit.se.inventoryservice.exceptions.InventoryException;
import iuh.fit.se.inventoryservice.exceptions.ResourceNotFoundException;
import iuh.fit.se.inventoryservice.repositories.InventoryRepository;
import iuh.fit.se.inventoryservice.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public InventoryResponse save(InventoryRequest inventoryRequest) {
        // Check if product already exists in inventory
        Inventory existingInventory = inventoryRepository.findByProductId(inventoryRequest.getProductId())
                .orElse(null);

        if (existingInventory != null) {
            // Update existing inventory
            existingInventory.setQuantity(inventoryRequest.getQuantity());
            existingInventory.setLocation(inventoryRequest.getLocation());
            existingInventory.setStatus(determineStatus(inventoryRequest.getQuantity()));
            return mapToInventoryResponse(inventoryRepository.save(existingInventory));
        } else {
            // Create new inventory
            Inventory inventory = Inventory.builder()
                    .productId(inventoryRequest.getProductId())
                    .quantity(inventoryRequest.getQuantity())
                    .location(inventoryRequest.getLocation())
                    .status(determineStatus(inventoryRequest.getQuantity()))
                    .build();
            return mapToInventoryResponse(inventoryRepository.save(inventory));
        }
    }

    @Override
    public InventoryResponse findById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::mapToInventoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
    }

    @Override
    public InventoryResponse findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::mapToInventoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
    }

    @Override
    public List<InventoryResponse> findAll() {
        List<Inventory> inventories = inventoryRepository.findAll();
        if (inventories.isEmpty()) {
            throw new ResourceNotFoundException("No inventory records found");
        }
        return inventories.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long productId, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        int newQuantity = inventory.getQuantity() + quantityChange;
        
        if (newQuantity < 0) {
            throw new InventoryException("Insufficient inventory for product id: " + productId + 
                    ". Requested: " + Math.abs(quantityChange) + ", Available: " + inventory.getQuantity());
        }
        
        inventory.setQuantity(newQuantity);
        inventory.setStatus(determineStatus(newQuantity));
        
        return mapToInventoryResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public boolean processOrder(OrderMessage orderMessage) {
        try {
            // Only process if the order status is not completed or cancelled
            if ("PENDING".equals(orderMessage.getStatus()) || "PROCESSING".equals(orderMessage.getStatus())) {
                // Decrease inventory by order quantity (negative number decreases inventory)
                updateInventory(orderMessage.getProductId(), -orderMessage.getQuantity());
                return true;
            }
            // If the order is cancelled, increase the inventory
            else if ("CANCELLED".equals(orderMessage.getStatus())) {
                // Increase inventory by order quantity (positive number increases inventory)
                updateInventory(orderMessage.getProductId(), orderMessage.getQuantity());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Failed to process order: " + e.getMessage());
            return false;
        }
    }

    // Helper method to map Inventory entity to InventoryResponse DTO
    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .location(inventory.getLocation())
                .status(inventory.getStatus())
                .build();
    }
    
    // Helper method to determine inventory status based on quantity
    private String determineStatus(Integer quantity) {
        if (quantity <= 0) {
            return "OUT_OF_STOCK";
        } else if (quantity < 10) {
            return "LOW_STOCK";
        } else {
            return "AVAILABLE";
        }
    }
}