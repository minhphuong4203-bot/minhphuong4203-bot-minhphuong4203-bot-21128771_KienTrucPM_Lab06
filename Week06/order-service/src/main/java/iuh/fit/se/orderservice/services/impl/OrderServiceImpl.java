package iuh.fit.se.orderservice.services.impl;

import iuh.fit.se.orderservice.entities.Order;
import iuh.fit.se.orderservice.repositories.OrderRepository;
import iuh.fit.se.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
