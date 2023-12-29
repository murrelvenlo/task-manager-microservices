package fact.it.userservice.controller;

import fact.it.userservice.dto.LoginRequest;
import fact.it.userservice.dto.LoginResponse;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.exception.UserRegistrationException;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.service.AuthService;
import fact.it.userservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        try {
            // Validate user request
            Map<String, List<String>> validationErrors = userValidator.validateUserRequest(userRequest);

            // If validation succeeds, proceed with user registration
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
            }

            UserEntity newUser = authService.registerUser(userRequest);
            String fullName = newUser.getFirstName() + " " + newUser.getLastName();
            String successMessage = "The user, " + fullName + " successfully created.";
            return ResponseEntity.ok(successMessage);
        } catch (UserRegistrationException e) {
            // Handle the validation exception
            // Log or print the exception message for debugging
            System.out.println("Exception Message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions if needed
            System.out.println("Unexpected Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password!");
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if (authService.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/context")
    public UserResponse getCurrentUserContext() {
        return authService.getCurrentUser();
    }
}
