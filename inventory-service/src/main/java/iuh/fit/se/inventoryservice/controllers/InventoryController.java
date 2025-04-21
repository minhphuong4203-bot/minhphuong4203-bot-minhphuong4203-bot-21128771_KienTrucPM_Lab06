package iuh.fit.se.inventoryservice.controllers;

import iuh.fit.se.inventoryservice.dtos.InventoryRequest;
import iuh.fit.se.inventoryservice.dtos.InventoryResponse;
import iuh.fit.se.inventoryservice.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest inventoryRequest) {
        InventoryResponse inventoryResponse = inventoryService.save(inventoryRequest);
        return new ResponseEntity<>(inventoryResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        InventoryResponse inventoryResponse = inventoryService.findById(id);
        return ResponseEntity.ok(inventoryResponse);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        InventoryResponse inventoryResponse = inventoryService.findByProductId(productId);
        return ResponseEntity.ok(inventoryResponse);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<InventoryResponse> inventoryResponses = inventoryService.findAll();
        return ResponseEntity.ok(inventoryResponses);
    }

    @PutMapping("/product/{productId}/quantity/{quantity}")
    public ResponseEntity<InventoryResponse> updateInventoryQuantity(
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        InventoryResponse inventoryResponse = inventoryService.updateInventory(productId, quantity);
        return ResponseEntity.ok(inventoryResponse);
    }
}