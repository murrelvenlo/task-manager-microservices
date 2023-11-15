package fact.it.userservice.service.impl;

import fact.it.userservice.dto.MailDto;
import fact.it.userservice.dto.UserDTO;
import fact.it.userservice.dto.UserRequest;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.exception.UserNotFoundException;
import fact.it.userservice.model.User;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final WebClient webClient;
    @Override
    public User registerUser(UserRequest userRequest) {
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .phone(userRequest.getPhone())
                .userCode(UUID.randomUUID())
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
                .messageBody("Account Created")
                .messageBody("Dear " + userRequest.getFirstName() + ",\nYour account has been successfully created.")
                .build();

        // Send the email using WebClient to the mail-service
        webClient.post()
                .uri("http://localhost:8082/api/email/send-email")
                .bodyValue(mailDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(Long userId, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userRequest.getFirstName() != null) {
                user.setFirstName(userRequest.getFirstName());
            }
            if (userRequest.getLastName() != null) {
                user.setLastName(userRequest.getLastName());
            }
            if (userRequest.getUsername() != null) {
                user.setUsername(userRequest.getUsername());
            }
            if (userRequest.getEmail() != null) {
                user.setEmail(userRequest.getEmail());
            }
            if (userRequest.getPassword() != null) {
                user.setPassword(userRequest.getPassword());
            }
            if (userRequest.getPhone() != null) {
                user.setPhone(userRequest.getPhone());
            }
            if (userRequest.getTaskCode() != null) {
                user.setTaskCode(userRequest.getTaskCode());
            }

            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }


    @Override
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    @Override
    public UserResponse findUserByCode(UUID userCode) {
        User user = userRepository.findByUserCode(userCode);
        if (user != null) {
            return mapper.map(user, UserResponse.class);
        }
        return null;
    }
}
