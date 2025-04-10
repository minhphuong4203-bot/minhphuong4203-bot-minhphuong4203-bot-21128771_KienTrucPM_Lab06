package iuh.fit.se.orderservice.services.impl;

import iuh.fit.se.orderservice.entities.Order;
import iuh.fit.se.orderservice.repositories.OrderRepository;
import iuh.fit.se.orderservice.services.OrderService;
import iuh.fit.se.orderservice.dto.OrderMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Value("${app.rabbit.queue-name}")
    private String queueName;

    @Value("${app.rabbit.exchange-name}")
    private String exchangeName;

    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Order findById(long id) {
        // Tìm kiếm đơn hàng theo ID
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Tạo OrderMessage để gửi vào RabbitMQ
        OrderMessage orderMessage = OrderMessage.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();

        // Gửi thông điệp vào RabbitMQ sau khi tìm thấy đơn hàng
        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);

        return order;
    }

    @Override
    public Order save(Order order) {
        // Lưu đơn hàng vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(order);

        // Tạo OrderMessage từ thông tin của đơn hàng vừa lưu
        OrderMessage orderMessage = OrderMessage.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();

        // Gửi thông điệp vào RabbitMQ sau khi lưu thành công
        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);

        return savedOrder;
    }
}
