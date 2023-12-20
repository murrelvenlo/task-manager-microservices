package fact.it.apigateway.filter;

import fact.it.apigateway.util.JwtUtil;
import fact.it.apigateway.validator.RouterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouterValidator routerValidator;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (routerValidator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing authorization header");
                }

                String autHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (autHeader != null && autHeader.startsWith("Bearer ")) {
                    autHeader = autHeader.substring(7);
                }
                try {
//                    //REST call to AUTH service
//                    template.getForObject("http://AUTTH-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(autHeader);

                    // Extract user details from the validated token
                    String userRole = jwtUtil.extractUserRole(autHeader);

                    // Add user details to request headers
                    exchange.getRequest().mutate()
                            .header("X-User-Role", userRole)
                            .build();

                } catch (Exception e) {
                    System.out.println("invalid access...!");
                    throw new RuntimeException("un authorized access to application");
                }
            }
            return chain.filter(exchange);
        }));
    }

    public static class Config {

    }
}
