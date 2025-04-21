package iuh.fit.se.inventoryservice.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.inventoryservice.dtos.OrderMessage;
import iuh.fit.se.inventoryservice.services.InventoryService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderMessageListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = "${app.rabbit.queue-name}")
    public void receiveOrder(Message message) {
        try {
            String body = new String(message.getBody());
            System.out.println("Received order message: " + body);
            
            OrderMessage orderMessage = objectMapper.readValue(body, OrderMessage.class);
            
            boolean processed = inventoryService.processOrder(orderMessage);
            
            if (processed) {
                System.out.println("Successfully processed order: " + orderMessage.getId() + 
                    " for product: " + orderMessage.getProductId() + 
                    " with quantity: " + orderMessage.getQuantity());
            } else {
                System.out.println("Order not processed. Status: " + orderMessage.getStatus());
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing order message: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
        }
    }
}