package fact.it.userservice;

import fact.it.userservice.controller.AuthController;
import fact.it.userservice.dto.LoginRequest;
import fact.it.userservice.dto.LoginResponse;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.model.Role;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.AuthService;
import fact.it.userservice.service.impl.AuthServiceImpl;
import fact.it.userservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private AuthServiceImpl authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_SuccessfulRegistration() {
        // Arrange
        UserRequest userRequest = new UserRequest("John", "Doe", "john.doe", "john@example.com", "password", "1234567890", "USER", "1234454548947784");

        // Act
        UserEntity result = authService.registerUser(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertTrue(new BCryptPasswordEncoder().matches("password", result.getPassword()));
        assertEquals("1234567890", result.getPhone());
        assertEquals(Role.USER, result.getRole());
        assertNotNull(result.getUserCode());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

//    @Test
//    public void testAuthenticateUser() {
//        // Mock the behavior of dependencies
//        LoginRequest request = new LoginRequest("test@example.com", "password");
//        UserDetails userDetails = new User(
//                "test@example.com", "password", new ArrayList<>()
//        );
//
//        when((Publisher<?>)userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
//
//        String fakeToken = "fakeJwtToken";
//        when(jwtUtil.generateToken("test@example.com")).thenReturn(fakeToken);
//
//        UserEntity fakeUser = new UserEntity();
//        when((Publisher<?>)userRepository.findFirstByEmail("test@example.com")).thenReturn(fakeUser);
//
//        UserResponse fakeUserResponse = new UserResponse();
//        when((Publisher<?>)mapper.map(fakeUser, UserResponse.class)).thenReturn(fakeUserResponse);
//
//        // Call the method to test
//        LoginResponse response = authService.authenticateUser(request);
//
//        // Verify that the authenticationManager was called with the correct arguments
//        verify(authenticationManager).authenticate(
//                new UsernamePasswordAuthenticationToken("test@example.com", "password"));
//
//        // Verify that the other methods were called with the correct arguments
//        verify(userDetailsService).loadUserByUsername("test@example.com");
//        verify(jwtUtil).generateToken("test@example.com");
//        verify(userRepository).findFirstByEmail("test@example.com");
//        verify(mapper).map(fakeUser, UserResponse.class);
//
//        // Verify that the response is as expected
//        assertEquals(fakeToken, response.jwtToken());
//        assertEquals(fakeUserResponse, response.user());
//    }

//    @Test
//    public void testLogin_BadCredentials() {
//        // Mock the behavior of the AuthService to throw BadCredentialsException
//        LoginRequest request = new LoginRequest("test@example.com", "password");
//
//        when((Publisher<?>) authenticationManager.authenticate(any()))
//                .then();
//
//        // Call the endpoint
//        ResponseEntity<?> responseEntity = authController.login(request);
//
//        // Verify that the response is as expected
//        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
//        assertEquals("Incorrect email or password!", responseEntity.getBody());
//    }
}
