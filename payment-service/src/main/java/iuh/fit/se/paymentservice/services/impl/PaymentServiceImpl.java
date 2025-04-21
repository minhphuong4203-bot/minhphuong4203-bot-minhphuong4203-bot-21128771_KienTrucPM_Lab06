package iuh.fit.se.paymentservice.services.impl;

import iuh.fit.se.paymentservice.dtos.PaymentRequest;
import iuh.fit.se.paymentservice.dtos.PaymentResponse;
import iuh.fit.se.paymentservice.entities.Payment;
import iuh.fit.se.paymentservice.entities.PaymentStatus;
import iuh.fit.se.paymentservice.exceptions.PaymentException;
import iuh.fit.se.paymentservice.exceptions.ResourceNotFoundException;
import iuh.fit.se.paymentservice.repositories.PaymentRepository;
import iuh.fit.se.paymentservice.services.PaymentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange-name}")
    private String exchangeName;

    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Generate unique transaction ID
        String transactionId = UUID.randomUUID().toString();
        
        // Create payment entity with PENDING status initially
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .customerId(paymentRequest.getCustomerId())
                .amount(paymentRequest.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(paymentRequest.getPaymentMethod())
                .transactionId(transactionId)
                .paymentDate(LocalDateTime.now())
                .description(paymentRequest.getDescription())
                .build();
        
        // Process payment logic (simulate payment gateway interaction)
        boolean isPaymentSuccessful = processExternalPayment(payment);
        
        if (isPaymentSuccessful) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            throw new PaymentException("Payment processing failed for transaction: " + transactionId);
        }
        
        // Save payment to database
        Payment savedPayment = paymentRepository.save(payment);
        
        // Send payment notification to RabbitMQ
        sendPaymentNotification(savedPayment);
        
        return mapToPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        
        return mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("Payments", "orderId", orderId);
        }
        
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("Payments", "customerId", customerId);
        }
        
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        // Check if payment is eligible for refund
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Only completed payments can be refunded");
        }
        
        // Process refund logic (simulate refund gateway interaction)
        boolean isRefundSuccessful = processRefund(payment);
        
        if (isRefundSuccessful) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setDescription(payment.getDescription() + " - Refunded");
        } else {
            throw new PaymentException("Refund processing failed for payment ID: " + paymentId);
        }
        
        // Save updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Send payment notification to RabbitMQ
        sendPaymentNotification(updatedPayment);
        
        return mapToPaymentResponse(updatedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        // Check if payment is eligible for cancellation
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Only pending payments can be cancelled");
        }
        
        // Process cancellation
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setDescription(payment.getDescription() + " - Cancelled");
        
        // Save updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Send payment notification to RabbitMQ
        sendPaymentNotification(updatedPayment);
        
        return mapToPaymentResponse(updatedPayment);
    }
    
    // Helper methods
    private boolean processExternalPayment(Payment payment) {
        // Simulate external payment gateway interaction
        // In a real-world scenario, this would integrate with a payment gateway API
        try {
            // Simulate successful payment processing for most cases
            return Math.random() > 0.1; // 90% success rate
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean processRefund(Payment payment) {
        // Simulate refund processing with external gateway
        // In a real-world scenario, this would integrate with a payment gateway API
        try {
            // Simulate successful refund processing for most cases
            return Math.random() > 0.2; // 80% success rate
        } catch (Exception e) {
            return false;
        }
    }
    
    private void sendPaymentNotification(Payment payment) {
        // Send payment notification via RabbitMQ
        PaymentResponse paymentResponse = mapToPaymentResponse(payment);
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, paymentResponse);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send payment notification: " + e.getMessage());
        }
    }
    
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .description(payment.getDescription())
                .build();
    }
}