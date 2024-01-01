package fact.it.userservice.validator;


import fact.it.userservice.dto.UpdateUserDto;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public Map<String, List<String>> validateUserRequest(UserRequest userRequest) {
        Map<String, List<String>> validationErrors = new HashMap<>();

        validateField(() -> userRepository.existsByEmail(userRequest.getEmail()),
                "email", "User with the same email already exists", validationErrors);

        validateField(() -> PhoneNumberValidator.getInstance().isPhoneNumberValid(userRequest.getPhone()),
                "phone", "Invalid phone number", validationErrors);


        validatePassword(userRequest.getPassword(), "password", validationErrors);

        return validationErrors;
    }

    public Map<String, List<String>> validateUserUpdate(UpdateUserDto updateUserDto) {
        Map<String, List<String>> validationErrors = new HashMap<>();

        // Validate phone number
        validateField(() -> PhoneNumberValidator.getInstance().isPhoneNumberValid(updateUserDto.getPhone()),
                "phone", "Invalid phone number", validationErrors);

        // Validate password
        validatePassword(updateUserDto.getPassword(), "password", validationErrors);

        return validationErrors;
    }


    private void validateField(BooleanSupplier condition, String fieldName, String errorMessage, Map<String, List<String>> validationErrors) {
        if (condition.getAsBoolean()) {
            validationErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        }
    }

    private void validatePassword(String password, String fieldName, Map<String, List<String>> validationErrors) {
        // Password length validation
        if (password.length() < 6) {
            validationErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add("Password must be at least 6 characters long");
        }
    }
}
