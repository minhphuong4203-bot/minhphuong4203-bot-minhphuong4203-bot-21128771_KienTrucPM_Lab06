package iuh.fit.se.inventoryservice.exceptions;

public class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }
    
    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}