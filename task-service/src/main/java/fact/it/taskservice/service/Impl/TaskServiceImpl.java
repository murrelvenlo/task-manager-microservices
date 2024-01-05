package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.*;
import fact.it.taskservice.exception.DuplicateEntityException;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final MongoTemplate mongoTemplate;
    private final WebClient webClient;
    @Value("${emailservice.baseurl}")
    private String emailServiceBaseUrl;
    @Value("${teamservice.baseurl}")
    private String teamServiceBaseUrl;
    @PostConstruct
    public void loadData() {
        if (taskRepository.count() <= 0) {
            Task task = Task.builder()
                    .taskCode("task-123456")
                    .name("Microservice Spring Boot")
                    .description("Develop a Microservice backend for a project, with a minimum of 3 microservices for individual work or 4 for team projects of two. Choose a theme excluding Orders or Inventory, and create a demo video illustrating the project's structure, functionality, and features.")
                    .rNumber("r0781309")
                    .creationDate(LocalDateTime.now())
                    .dueDate(new Date("2024-01-07T11:11:11.717+00:00"))
                    .build();

            Task task1 = Task.builder()
                    .taskCode("task-0114738")
                    .name("Exam AI")
                    .description("Study for Exam AI.")
                    .rNumber("r0781309")
                    .creationDate(LocalDateTime.now())
                    .dueDate(new Date("2024-01-11T11:11:11.717+00:00"))
                    .build();

            Task task2 = Task.builder()
                    .taskCode("task-0246139")
                    .name("Project 4.0")
                    .description("Description for Project 4.0.")
                    .rNumber("r0123456")
                    .creationDate(LocalDateTime.now())
                    .dueDate(new Date("2024-01-16T11:11:11.717+00:00"))
                    .build();

            taskRepository.save(task);
            taskRepository.save(task1);
            taskRepository.save(task2);
        }
    }
    @Override
    public void createTask(TaskRequest taskRequest) {
        // Check if a task with the same name and status already exists
        if (taskRepository.existsByNameAndStatus(taskRequest.getName(), taskRequest.getStatus())) {
            throw new DuplicateEntityException("Task", "Task with the same name and status already exists");
        }

        // Generate a random 7-digit number
        String randomDigits = generateRandomDigits(7);

        // Build the taskCode with the "task-" prefix
        String taskCode = "task-" + randomDigits;

        Task task = Task.builder()
                .taskCode(taskCode)
                .name(taskRequest.getName())
                .status(taskRequest.getStatus())
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description(taskRequest.getDescription())
                .rNumber(taskRequest.getRNumber() != null ? taskRequest.getRNumber() : null)
                .build();

        taskRepository.save(task);
        // sendTaskCreationEmail(taskRequest, taskCode);
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

            taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found for taskCode: " + taskCode);
        }
    }

    @Override
    public List<TaskResponse> getAllTasksByrNumber(String rNumber) {
        List<Task> tasks = taskRepository.findAllByrNumber(rNumber);

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
        Task task = taskRepository.findTaskByTaskCode(taskCode);

        if (task != null){
            taskRepository.deleteByTaskCode(taskCode);
        } else {
            throw new RuntimeException("Task not found with task code: " + taskCode);
        }
    }


    private void sendTaskCreationEmail(TaskRequest taskRequest, String rNumber) {

        try {
            // Retrieve member information for the associated task
            MemberDto member = webClient.get()
                    .uri("http://" + teamServiceBaseUrl + "/api/member/get/{rNumber}", rNumber)
                    .retrieve()
                    .bodyToMono(MemberDto.class)
                    .block();

            // Log user information
            System.out.println("Retrieved member: " + member);

            // Create a MailDto with task and user information
            assert member != null;
            MailDto mailDto = MailDto.builder()
                    .recipient(member.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + member.getFirstName() + ",\nA new task has been created: " + taskRequest.getName())
                    .build();

            // Send the email using WebClient to the mail-service
            webClient.post()
                    .uri("http://" + emailServiceBaseUrl + "/api/email/send-email")
                    .bodyValue(mailDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    // generate random digits
    private String generateRandomDigits(int n) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
