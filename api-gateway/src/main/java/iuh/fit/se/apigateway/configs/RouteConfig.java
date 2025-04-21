package iuh.fit.se.apigateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Identity service routes
                .route("identity-service", r -> r.path("/auth/**")
                        .uri("lb://identity-service"))
                
                // Product service routes - requires USER role
                .route("product-service", r -> r.path("/products/**")
                        .uri("lb://product-service"))
                
                // Order service routes - requires authentication
                .route("order-service", r -> r.path("/orders/**")
                        .uri("lb://order-service"))
                
                // Payment service routes - requires authentication
                .route("payment-service", r -> r.path("/api/payments/**")
                        .uri("lb://payment-service"))
                
                // Inventory service routes - requires authentication
                .route("inventory-service", r -> r.path("/inventory/**")
                        .uri("lb://inventory-service"))
                
                // Shipping service routes - requires authentication
                .route("shipping-service", r -> r.path("/shipping/**")
                        .uri("lb://shipping-service"))
                
                // Customer service routes - requires authentication
                .route("customer-service", r -> r.path("/customers/**")
                        .uri("lb://customer-service"))
                
                .build();
    }
}