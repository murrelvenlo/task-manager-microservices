package fact.it.userservice.controller;

import fact.it.userservice.dto.UpdateUserDto;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.exception.UserRegistrationException;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.service.UserService;
import fact.it.userservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
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

            UserEntity newUser = userService.registerUser(userRequest);
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


    @DeleteMapping("/delete/{userCode}")
    public ResponseEntity<String> deleteUser(@PathVariable String userCode) {
        try {
            userService.deleteUserByCode(userCode);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{userCode}")
    public ResponseEntity<?> updateUser(@PathVariable String userCode, @RequestBody UpdateUserDto updateUserDto) {
        try {
            // Validate user update request
            Map<String, List<String>> validationErrors = userValidator.validateUserUpdate(updateUserDto);

            // If validation succeeds, proceed with user update
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
            }

            userService.updateUser(userCode, updateUserDto);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
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



    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/code/{userCode}")
    public UserResponse findUserByCode(@PathVariable String userCode) {
        return userService.findUserByCode(userCode);
    }

    @GetMapping("/email/{email}")
    public UserResponse findUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

}
