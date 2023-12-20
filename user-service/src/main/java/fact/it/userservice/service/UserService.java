package fact.it.userservice.service;

import fact.it.userservice.dto.UpdateUserDto;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.model.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void deleteUserByCode(String userCode);
    List<UserResponse> getAllUsers();
    UserResponse findUserByCode(String userCode);
    UserResponse getUserByEmail(String email);
    void updateUser(String userCode, UpdateUserDto updateUserDto);
}
