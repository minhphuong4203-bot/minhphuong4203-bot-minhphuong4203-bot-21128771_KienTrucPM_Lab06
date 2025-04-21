package iuh.fit.se.paymentservice.services;

import iuh.fit.se.paymentservice.dtos.PaymentRequest;
import iuh.fit.se.paymentservice.dtos.PaymentResponse;
import iuh.fit.se.paymentservice.entities.Payment;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentById(Long id);
    List<PaymentResponse> getPaymentsByOrderId(Long orderId);
    List<PaymentResponse> getPaymentsByCustomerId(Long customerId);
    PaymentResponse refundPayment(Long paymentId);
    PaymentResponse cancelPayment(Long paymentId);
}