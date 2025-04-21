package iuh.fit.se.paymentservice.messaging;

import iuh.fit.se.paymentservice.dtos.PaymentRequest;
import iuh.fit.se.paymentservice.dtos.PaymentResponse;
import iuh.fit.se.paymentservice.services.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageListener {
    
    private final PaymentService paymentService;
    
    @Autowired
    public PaymentMessageListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * Listens to payment requests from the order service and processes them
     * 
     * @param paymentRequest The payment request from order service
     * @return PaymentResponse with the result of the payment processing
     */
    @RabbitListener(queues = "${app.rabbit.queue-name}")
    public PaymentResponse processPaymentMessage(PaymentRequest paymentRequest) {
        System.out.println("Received payment request: " + paymentRequest);
        return paymentService.processPayment(paymentRequest);
    }
}