package iuh.fit.se.emailservice.services.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.emailservice.dtos.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceListener.class);
    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;

        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);

            logger.info(
                    String.format(
                            "Notifying user with id = %d ordered with orderId: %d has productId: %d with quantity: %d and status = %s",
                            messageResponse.getCustomerId(),
                            messageResponse.getId(),
                            messageResponse.getProductId(),
                            messageResponse.getQuantity(),
                            messageResponse.getStatus()
                    )
            );
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }
}
