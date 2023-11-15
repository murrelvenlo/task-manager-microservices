package fact.it.userservice.controller;

import fact.it.userservice.dto.UserDTO;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.exception.UserNotFoundException;
import fact.it.userservice.model.User;
import fact.it.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        User newUser = userService.registerUser(userRequest);

        // Check if user registration was successful
        if (newUser != null) {
            String fullName = newUser.getFirstName() + " " + newUser.getLastName();
            String successMessage = "The user, " + fullName + " successfully created.";
            return ResponseEntity.ok(successMessage);
        } else {
            // Handle the case where user registration failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed.");
        }
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/code/{userCode}")
    public UserResponse findUserByCode(@PathVariable UUID userCode) {
        return userService.findUserByCode(userCode);
    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        userService.updateUser(userId, userRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
