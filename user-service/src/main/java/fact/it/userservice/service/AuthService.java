package fact.it.userservice.service;

import fact.it.userservice.dto.LoginRequest;
import fact.it.userservice.dto.LoginResponse;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    UserEntity registerUser(UserRequest userRequest);
    void sendUserCreationEmail(UserRequest userRequest);
    LoginResponse authenticateUser(LoginRequest loginRequest);
    Boolean isTokenValid(String token, UserDetails userDetails);
    Boolean validateToken(String token);
    UserResponse getCurrentUser();
}
