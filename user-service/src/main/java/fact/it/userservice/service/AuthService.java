package fact.it.userservice.service;

import fact.it.userservice.dto.LoginRequest;
import fact.it.userservice.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest request);
}
