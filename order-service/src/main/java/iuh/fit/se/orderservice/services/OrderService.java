package iuh.fit.se.orderservice.services;

import iuh.fit.se.orderservice.entities.Order;

public interface OrderService {
    public Order save(Order order);
    public Order findById(long id);
}
