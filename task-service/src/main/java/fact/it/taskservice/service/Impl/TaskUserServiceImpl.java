package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.*;
import fact.it.taskservice.exception.TaskNotFoundException;
import fact.it.taskservice.exception.UserNotFoundException;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskUserServiceImpl implements TaskUserService {
    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;
    @Override
    public void createTask(TaskRequest taskRequest, String userCode) {
        UserDto user = webClient.get()
                .uri("http://localhost:8081/api/users/code/{userCode}", userCode)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if (user != null){

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();

            // Convert LocalDateTime to Date
            Date currentDateAndTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            Task task = Task.builder()
                    .taskCode(UUID.randomUUID())
                    .name(taskRequest.getName())
                    .status(taskRequest.getStatus())
                    .creationDate(currentDateAndTime)
                    .dueDate(new Date())
                    .description(taskRequest.getDescription())
                    .isProfessional(taskRequest.isProfessional())
                    .userCode(userCode)
                    .build();

            taskRepository.save(task);
            sendTaskCreationEmail(taskRequest, UUID.fromString(userCode));
        } else {
            throw new UserNotFoundException("User not found for userCode: " + userCode);
        }
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void sendTaskCreationEmail(TaskRequest taskRequest, UUID userCode) {

        try {
            // Retrieve user information for the associated task
            UserDto user = webClient.get()
                    .uri("http://localhost:8081/api/users/code/{userCode}", userCode)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();

            // Log user information
            System.out.println("Retrieved user: " + user);

            // Create a MailDto with task and user information
            assert user != null;
            MailDto mailDto = MailDto.builder()
                    .recipient(user.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + user.getFirstName() + ",\nA new task has been created: " + taskRequest.getName())
                    .build();

            // Send the email using WebClient to the mail-service
            webClient.post()
                    .uri("http://localhost:8082/api/email/send-email")
                    .bodyValue(mailDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(String taskCode, TaskRequest taskRequest, String userCode) {
        UserDto user = webClient.get()
                .uri("http://localhost:8081/api/users/code/{userCode}", userCode)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if (user != null) {
            Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskByTaskCode(UUID.fromString(taskCode)));
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();
                task.setName(taskRequest.getName());
                task.setStatus(taskRequest.getStatus());
                task.setDueDate(taskRequest.getDueDate());
                task.setDescription(taskRequest.getDescription());
                task.setProfessional(taskRequest.isProfessional());
                task.setUserCode(userCode); // Ensure the userCode is updated if necessary

                taskRepository.save(task);
            } else {
                throw new TaskNotFoundException("Task not found for taskCode: " + taskCode);
            }
        } else {
            throw new UserNotFoundException("User not found for userCode: " + userCode);
        }
    }

    @Override
    public void deleteTask(String taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()){
            taskRepository.deleteById(taskId);
        } else {
            throw new RuntimeException("Task not found with task id: " + taskId);
        }
    }

    @Override
    public UserTaskResponse getUserWithTask(String taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isPresent()){
            Task taskData = task.get();
            UserDto user = getUserByCode(taskData.getUserCode());

            if (user != null){
                return UserTaskResponse.builder()
                        .id(taskData.getId())
                        .taskCode(taskData.getTaskCode())
                        .name(taskData.getName())
                        .description(taskData.getDescription())
                        .creationDate(taskData.getCreationDate())
                        .dueDate(taskData.getDueDate())
                        .isProfessional(taskData.isProfessional())
                        .status(taskData.getStatus())
                        .userCode(user.getUserCode().toString())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .phone(user.getPhone())
                        .taskCodes(user.getTaskCodes())
                        .build();
            }
        }
        return null;
    }


    public UserDto getUserByCode(String userCode) {
        return webClient.get()
                .uri("http://localhost:8081/api/users/code/{userCode}", userCode)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}
