package fact.it.apigateway.filter;

import fact.it.apigateway.util.JwtUtil;
import fact.it.apigateway.validator.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
//    @Autowired
//    private RestTemplate template;
    @Autowired
    private JwtUtil jwtUtil;
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = null;
            if (validator.isSecured.test(exchange.getRequest())){
                // header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    // Call user-service
//                    template.getForObject("http://localhost:8081/api/auth/validate-token?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);


                    request = exchange.getRequest()
                            .mutate()
                            .header("authenticatedUser", jwtUtil.extractUsername(authHeader))
                            .build();

                } catch (Exception ex){
                    throw new RuntimeException("Unauthorized access");
                }
            }
            // Request object to filter
            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    public static  class Config{}
}
