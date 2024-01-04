package fact.it.apigateway.config;

import fact.it.apigateway.filter.AuthenticationFilter;
import fact.it.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

//    @Autowired
//    private AuthenticationFilter authenticationFilter;
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Bean
//    @Order(1)
//    public SecurityWebFilterChain jwtSecurityFilterChain(ServerHttpSecurity http) {
//        return http
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/api/auth/**").permitAll() // Adjust the path as needed
//                        .anyExchange().authenticated())
//                .addFilterBefore((WebFilter) authenticationFilter,SecurityWebFiltersOrder.AUTHENTICATION)
////                .addFilterAt((WebFilter) authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .build();
//    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/member/add", "/api/team/get/all").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()))
                .build();
    }

//    @Bean
//    public AuthenticationFilter jwtAuthenticationFilter() {
//        JwtUtil jwtUtil = new JwtUtil();
//        jwtUtil.setSecret(jwtSecret);
//        return new AuthenticationFilter(jwtUtil);
//    }


//    @Bean
//    public AuthenticationFilter authenticationFilter() {
//        return new AuthenticationFilter();
//    }
}
