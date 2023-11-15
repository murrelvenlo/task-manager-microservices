package fact.it.userservice.service;

import fact.it.userservice.dto.UserDTO;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User registerUser(UserRequest userRequest);
    void sendUserCreationEmail(UserRequest userRequest);
    List<UserResponse> getAllUsers();
    void updateUser(Long userId, UserRequest userRequest);
    void deleteUser(Long userId);
    UserResponse findUserByCode(UUID userCode);
}
