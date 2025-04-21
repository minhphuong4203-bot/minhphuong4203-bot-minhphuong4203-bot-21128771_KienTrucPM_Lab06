package iuh.fit.se.inventoryservice.configs;

import iuh.fit.se.inventoryservice.entities.Inventory;
import iuh.fit.se.inventoryservice.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public DataInitializer(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        // Check if we already have inventory data
        if (inventoryRepository.count() == 0) {
            // Add sample inventory data
            addSampleData();
        }
    }

    private void addSampleData() {
        // Create sample inventory items
        Inventory item1 = Inventory.builder()
                .productId(1L)
                .quantity(100)
                .location("Warehouse A")
                .status("AVAILABLE")
                .build();

        Inventory item2 = Inventory.builder()
                .productId(2L)
                .quantity(50)
                .location("Warehouse A")
                .status("AVAILABLE")
                .build();

        Inventory item3 = Inventory.builder()
                .productId(3L)
                .quantity(5)
                .location("Warehouse B")
                .status("LOW_STOCK")
                .build();

        Inventory item4 = Inventory.builder()
                .productId(4L)
                .quantity(0)
                .location("Warehouse B")
                .status("OUT_OF_STOCK")
                .build();

        // Save all items
        inventoryRepository.save(item1);
        inventoryRepository.save(item2);
        inventoryRepository.save(item3);
        inventoryRepository.save(item4);

        System.out.println("Sample inventory data has been added!");
    }
}