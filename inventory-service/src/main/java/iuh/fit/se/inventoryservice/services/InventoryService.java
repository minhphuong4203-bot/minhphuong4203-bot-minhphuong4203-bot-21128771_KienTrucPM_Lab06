package iuh.fit.se.inventoryservice.services;

import iuh.fit.se.inventoryservice.dtos.InventoryRequest;
import iuh.fit.se.inventoryservice.dtos.InventoryResponse;
import iuh.fit.se.inventoryservice.dtos.OrderMessage;
import iuh.fit.se.inventoryservice.entities.Inventory;

import java.util.List;

public interface InventoryService {
    InventoryResponse save(InventoryRequest inventoryRequest);
    InventoryResponse findById(Long id);
    InventoryResponse findByProductId(Long productId);
    List<InventoryResponse> findAll();
    InventoryResponse updateInventory(Long productId, Integer quantity);
    boolean processOrder(OrderMessage orderMessage);
}