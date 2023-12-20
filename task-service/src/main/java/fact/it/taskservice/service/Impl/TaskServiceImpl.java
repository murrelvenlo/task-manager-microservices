package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.*;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;
    @Override
    public void createTask(TaskRequest taskRequest) {
        Task task = Task.builder()
                .taskCode(String.valueOf(UUID.randomUUID()))
                .name(taskRequest.getName())
                .status(taskRequest.getStatus())
                .creationDate(new Date())
                .dueDate(new Date())
                .description(taskRequest.getDescription())
                .isProfessional(taskRequest.isProfessional())
                .userCode(taskRequest.getUserCode() != null ? taskRequest.getUserCode().toString() : null)
                .build();

        taskRepository.save(task);
//        sendTaskCreationEmail(taskRequest, taskRequest.getTaskCode());
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasksByTaskCode(List<String> taskCode) {
        List<Task> tasks = taskRepository.findByTaskCodeIn(taskCode);

        return tasks.stream().map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse findTaskByTaskCode(String taskCode) {
        Task task = taskRepository.findTaskByTaskCode(taskCode);
        if (task != null){
            return mapper.map(task, TaskResponse.class);
        }
        return null;
    }


    @Override
    public void updateTask(String taskId, TaskRequest taskRequest) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()){
            Task task = taskOptional.get();

            if(taskRequest.getName() != null && !taskRequest.getName().isEmpty()) {
                task.setName(taskRequest.getName());
            }
            if(taskRequest.getDescription() != null && !taskRequest.getDescription().isEmpty()) {
                task.setDescription(taskRequest.getDescription());
            }
            if(taskRequest.getStatus() != null) {
                task.setStatus(taskRequest.getStatus());
            }
            if(taskRequest.getDueDate() != null) {
                task.setDueDate(taskRequest.getDueDate());
            }
            task.setProfessional(taskRequest.isProfessional()); // Assuming isProfessional is not null

            taskRepository.save(task);
        }
        else {
            throw new RuntimeException("Task not found with task id: " + taskId);
        }
    }

    @Override
    public void updateTaskByCode(String taskCode, TaskRequest taskRequest) {
        Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskByTaskCode(taskCode));

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setName(taskRequest.getName());
            task.setStatus(taskRequest.getStatus());
            task.setDueDate(taskRequest.getDueDate());
            task.setDescription(taskRequest.getDescription());
            task.setProfessional(taskRequest.isProfessional());

            taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found for taskCode: " + taskCode);
        }
    }

    @Override
    public List<TaskResponse> getAllTasksByUserCode(String userCode) {
        List<Task> tasks = taskRepository.findAllByUserCode(userCode);

        if (!tasks.isEmpty()) {
            List<TaskResponse> taskResponses = tasks.stream()
                    .map(task -> mapper.map(task, TaskResponse.class))
                    .collect(Collectors.toList());

            return taskResponses;
        } else {
            return Collections.emptyList();
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
    public void deleteTaskByCode(String taskCode) {
        Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskByTaskCode(taskCode));
        if (optionalTask.isPresent()){
            taskRepository.deleteById(taskCode);
        } else {
            throw new RuntimeException("Task not found with task code: " + taskCode);
        }
    }


    private void sendTaskCreationEmail(TaskRequest taskRequest, UUID userCode) {

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

}
