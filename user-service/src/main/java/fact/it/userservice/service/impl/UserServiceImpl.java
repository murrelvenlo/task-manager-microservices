package fact.it.userservice.service.impl;

import fact.it.userservice.dto.MailDto;
import fact.it.userservice.dto.UpdateUserDto;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.exception.UserNotFoundException;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    @Override
    @Transactional
    public void deleteUserByCode(String userCode) {
        Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUserCode(userCode));
        if (user.isPresent()){
            userRepository.deleteByUserCode(userCode);
        } else {
            throw new RuntimeException("User not found with task code: " + userCode);
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findUserByCode(String userCode) {
        UserEntity user = userRepository.findByUserCode(userCode);
        if (user != null) {
            return mapper.map(user, UserResponse.class);
        }
        return null;
    }

    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findFirstByEmail(email);
        if (user != null) {
            return mapper.map(user, UserResponse.class);
        }
        return null;
    }

    @Override
    public void updateUser(String userCode, UpdateUserDto updateUserDto) {
        UserResponse existingUser = findUserByCode(userCode);
        if (existingUser != null){
            UserEntity updatedUser = UserEntity.builder()
                    .firstName(updateUserDto.getFirstName())
                    .lastName(updateUserDto.getLastName())
                    .username(updateUserDto.getUsername())
                    .password(new BCryptPasswordEncoder().encode(updateUserDto.getPassword()))
                    .phone(updateUserDto.getPhone())
                    .build();

            userRepository.save(updatedUser);
        } else {
            throw new RuntimeException("User with userCode " + userCode + " not found");
        }
    }
}
