package iuh.fit.se.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMessage {
    private Long id;
    private Long customerId;
    private Long productId;
    private Integer quantity;
    private String status;
}