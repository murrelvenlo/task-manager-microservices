package fact.it.userservice.service.impl;

import fact.it.userservice.dto.MailDto;
import fact.it.userservice.dto.TaskDTO;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.AuthService;
import fact.it.userservice.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl implements UserTaskService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;
    private final AuthService authService;

    private static final Logger log = LoggerFactory.getLogger(UserTaskServiceImpl.class);

    @Override
    public void addTaskForUser(TaskDTO taskDto) {
        UserResponse currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            // Set the userCode on the taskDto before making the request
            taskDto.setUserCode(currentUser.getUserCode());

            try {
                webClient.post()
                        .uri("http://localhost:8080/api/tasks/add")
                        .bodyValue(taskDto)
                        .retrieve()
                        .toBodilessEntity()
                        .block(); // Block to wait for the result, handle this differently in a real application

                // Log the information or take any other action
                log.info("Task added successfully for user {}", currentUser.getUserCode());
                sendUsTaskCreationEmail(currentUser);

                // Optional: Update the original taskDto with the new taskCode
                // Note: In this case, you won't have the createdTaskDto as there's no response body
                // TaskDto createdTaskDto = responseEntity.getBody();
                // taskDto.setTaskCode(createdTaskDto.getTaskCode());
            } catch (Exception e) {
                // Handle exceptions appropriately
                log.error("Exception adding task for user: {}", e.getMessage());
                throw new RuntimeException("Exception adding task for user: " + e.getMessage());
            }
        }
    }

    //    @Override
//    public void addTaskForUser(String userCode, TaskDto taskDto) {
//        UserEntity user = userRepository.findByUserCode(userCode);
//
//        if (user != null) {
//            // Set the userCode on the taskDto before making the request
//            taskDto.setUserCode(userCode);
//
//            try {
//                webClient.post()
//                        .uri("http://localhost:8080/api/tasks/add")
//                        .bodyValue(taskDto)
//                        .retrieve()
//                        .toBodilessEntity()
//                        .block(); // Block to wait for the result, handle this differently in a real application
//
//                // Log the information or take any other action
//                log.info("Task added successfully for user {}", userCode);
//                sendUsTaskCreationEmail(user);
//
//                // Optional: Update the original taskDto with the new taskCode
//                // Note: In this case, you won't have the createdTaskDto as there's no response body
//                // TaskDto createdTaskDto = responseEntity.getBody();
//                // taskDto.setTaskCode(createdTaskDto.getTaskCode());
//            } catch (Exception e) {
//                // Handle exceptions appropriately
//                log.error("Exception adding task for user: {}", e.getMessage());
//                throw new RuntimeException("Exception adding task for user: " + e.getMessage());
//            }
//        }
//    }
    @Override
    public void updateTaskForUser(String taskCode, TaskDTO taskDto) {
        UserResponse currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            // Set the userCode on the taskRequest before making the request
            taskDto.setUserCode(currentUser.getUserCode());

            try {
                webClient.put()
                        .uri("http://localhost:8080/api/tasks/update/{taskCode}", taskCode)
                        .bodyValue(taskDto)
                        .retrieve()
                        .toBodilessEntity()
                        .block(); // Block to wait for the result, handle this differently in a real application

                // Log the information or take any other action
                String fullName = Objects.toString(currentUser.getFirstName(), "") + " " + Objects.toString(currentUser.getLastName(), "");

                log.info("Task updated successfully for user {}", fullName);
            } catch (Exception e) {
                // Handle exceptions appropriately
                log.error("Exception updating task for user: {}", e.getMessage());
                throw new RuntimeException("Exception updating task for user: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("User not found for userCode: " + currentUser.getUserCode());
        }
    }

    @Override
    public void deleteTaskForUser(String taskCode) {
        UserResponse currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            try {
                webClient.delete()
                        .uri("http://localhost:8080/api/tasks/delete/{taskCode}", taskCode)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
            } catch (Exception e) {

                // Handle exceptions appropriately
                log.error("Exception deleting task for user: {}", e.getMessage());
                throw new RuntimeException("Exception updating task for user: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("User not found for userCode: " + currentUser.getUserCode());
        }
    }

    @Override
    public List<TaskDTO> getTasksForCurrentUserByUserCode(String userCode) {
        try {
            // Get the authenticated user
            UserResponse currentUser = authService.getCurrentUser();

            if (currentUser != null) {
                // Make a request to the task-service to get tasks by user code
                List<TaskDTO> userTasks = webClient
                        .get()
                        .uri("http://localhost:8080/api/tasks/getByUserCode/{userCode}", userCode)
                        .retrieve()
                        .bodyToFlux(TaskDTO.class)
                        .collectList()
                        .block();

                // Filter tasks based on the provided userCode
                return userTasks.stream()
                        .filter(task -> userCode.equals(task.getUserCode()))
                        .collect(Collectors.toList());
            } else {
                // Handle the case where the current user is not found
                throw new RuntimeException("Current user not found");
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            throw new RuntimeException("Error retrieving tasks for user " + userCode, e);
        }
    }

    @Override
    public void senTaskReminder(TaskDTO taskDto, UserResponse user) {
        UserResponse currentUser = authService.getCurrentUser();

        // Check if the due date is today or within the next 3 days
        LocalDate dueDate = taskDto.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        if (dueDate.isEqual(today)) {
            // Create a MailDto with user information
            MailDto mailDto = MailDto.builder()
                    .recipient(user.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + user.getFirstName() + ", " + taskDto.getName() + ",\nYour task is due to today.")
                    .build();

            // Send the email using WebClient to the mail-service
            webClient.post()
                    .uri("http://localhost:8082/api/email/send-email")
                    .bodyValue(mailDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }
        else if(dueDate.isAfter(today) && dueDate.isBefore(threeDaysLater)) {
            // Create a MailDto with user information
            MailDto mailDto = MailDto.builder()
                    .recipient(user.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + user.getFirstName()  + ", " + taskDto.getName() + ",\nYour task is due in three days.")
                    .build();

            // Send the email using WebClient to the mail-service
            webClient.post()
                    .uri("http://localhost:8082/api/email/send-email")
                    .bodyValue(mailDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        }
    }

    @Override
    public List<TaskDTO> getAllTask() {
        List<TaskDTO> tasks = webClient
                .get()
                .uri("http://localhost:8080/api/tasks/get/all")
                .retrieve()
                .bodyToFlux(TaskDTO.class)
                .collectList()
                .block();
        return tasks != null ? tasks : Collections.emptyList();
    }

    private void sendUsTaskCreationEmail(UserResponse user) {
        // Create a MailDto with user information
        MailDto mailDto = MailDto.builder()
                .recipient(user.getEmail())
                .messageSubject("Task Created")
                .messageBody("Dear " + user.getFirstName() + ",\nYour task has been successfully created.")
                .build();

        // Send the email using WebClient to the mail-service
        webClient.post()
                .uri("http://localhost:8082/api/email/send-email")
                .bodyValue(mailDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
