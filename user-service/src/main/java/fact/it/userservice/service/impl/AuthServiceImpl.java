package fact.it.userservice.service.impl;

import fact.it.userservice.dto.*;
import fact.it.userservice.model.Role;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.AuthService;
import fact.it.userservice.service.jwt.UserDetailsServiceImpl;
import fact.it.userservice.util.JwtUtil;
import fact.it.userservice.validator.UserValidator;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserValidator userValidator;

    @Override
    public UserEntity registerUser(UserRequest userRequest) {
        UserEntity newUser = UserEntity.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(new BCryptPasswordEncoder().encode(userRequest.getPassword()))
                .phone(userRequest.getPhone())
                .userCode(UUID.randomUUID().toString())
                .role(Role.USER)
                .build();

        newUser = userRepository.save(newUser);
        sendUserCreationEmail(userRequest);
        return newUser;
    }

    @Override
    public void sendUserCreationEmail(UserRequest userRequest) {
        // Create a MailDto with user information
        MailDto mailDto = MailDto.builder()
                .recipient(userRequest.getEmail())
                .messageSubject("Account Created")
                .messageBody("Dear " + userRequest.getFirstName() + ",\nYour account has been successfully created.")
                .build();

        // Send the email using WebClient to the mail-service
        webClient.post()
                .uri("http://localhost:8083/api/email/send-email")
                .bodyValue(mailDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Include user information in the response
        UserEntity user = userRepository.findFirstByEmail(loginRequest.getEmail());

        UserResponse userResponse = mapper.map(user, UserResponse.class);

        return new LoginResponse(jwt, userResponse);
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = jwtUtil.extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public Boolean validateToken(String token) {
        String username = jwtUtil.extractUsername(token);

        if (StringUtils.isNotBlank(username)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtUtil.validateToken(token, userDetails);
        }

        return false;
    }
    public Boolean isTokenExpired(String token) {
        return !isTokenExpired(token);
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = ((User) authentication.getPrincipal()).getUsername();
        UserEntity user = userRepository.findFirstByEmail(currentUserEmail);

        return user != null ? mapper.map(user, UserResponse.class) : null;
    }
}
