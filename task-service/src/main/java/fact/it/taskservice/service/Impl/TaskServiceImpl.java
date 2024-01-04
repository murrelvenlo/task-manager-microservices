package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.*;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
                .rNumber(taskRequest.getRNumber() != null ? taskRequest.getRNumber() : null)
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
        Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskByTaskCode(taskCode));
        if (optionalTask.isPresent()){
            taskRepository.deleteById(taskCode);
        } else {
            throw new RuntimeException("Task not found with task code: " + taskCode);
        }
    }


    private void sendTaskCreationEmail(TaskRequest taskRequest, String rNumber) {

        try {
            // Retrieve member information for the associated task
            MemberDto member = webClient.get()
                    .uri("http://localhost:8081/api/member/get/{rNumber}", rNumber)
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
