package iuh.fit.se.apigateway.filters;

import iuh.fit.se.apigateway.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JWTGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JWTUtil jwtUtil;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // Allow public endpoints without authentication
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            return chain.filter(exchange);
        }
        
        // Get authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        // Check if the token exists and is valid
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtil.validateToken(token)) {
                // Extract user information and roles from the token
                String username = jwtUtil.extractUsername(token);
                List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token);
                
                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // Forward the token to downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .build();
                
                // Set security context
                return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
            }
        }
        
        // Authentication failed - return 401 Unauthorized
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    @Override
    public int getOrder() {
        return -100; // High precedence to ensure authentication happens early
    }
}
