package iuh.fit.se.emailservice.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.emailservice.dtos.MessageResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    @RabbitListener(queues = "${app.rabbit.queue-name}")
    public void receiveOrder(Message message) {
        String body = new String(message.getBody());
        System.out.println(body);
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;

        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);

            System.out.println(String.format(
                    "Notifying user with id = %d ordered with orderId: %d has productId: %d with quantity: %d and status = %s",
                    messageResponse.getCustomerId(),
                    messageResponse.getId(),
                    messageResponse.getProductId(),
                    messageResponse.getQuantity(),
                    messageResponse.getStatus()
            ));

        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }
}
