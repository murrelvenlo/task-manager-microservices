package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.*;
import fact.it.taskservice.exception.TaskNotFoundException;
import fact.it.taskservice.exception.UserNotFoundException;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskMemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskMemberServiceImpl implements TaskMemberService {
    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;
    @Value("${emailservice.baseurl}")
    private String emailServiceBaseUrl;
    @Value("${teamservice.baseurl}")
    private String teamServiceBaseUrl;
    @Override
    public void createTask(TaskRequest taskRequest, String rNumber) {
        MemberDto member = webClient.get()
                .uri("http://" + teamServiceBaseUrl + "/api/member/get/{rNumber}", rNumber)
                .retrieve()
                .bodyToMono(MemberDto.class)
                .block();

        if (member != null){

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();

            // Convert LocalDateTime to Date
            Date currentDateAndTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            Task task = Task.builder()
                    .taskCode(String.valueOf(UUID.randomUUID()))
                    .name(taskRequest.getName())
                    .status(taskRequest.getStatus())
                    .creationDate(now)
                    .dueDate(new Date())
                    .description(taskRequest.getDescription())
                    .rNumber(rNumber)
                    .build();

            taskRepository.save(task);
            sendTaskCreationEmail(taskRequest, rNumber);
        } else {
            throw new UserNotFoundException("Member not found for r_number: " + rNumber);
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
    public void sendTaskCreationEmail(TaskRequest taskRequest, String rNumber) {

        try {
            // Retrieve user information for the associated task
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

    @Override
    public void updateTask(String taskCode, TaskRequest taskRequest, String rNumber) {
        MemberDto member = webClient.get()
                .uri("http://" + teamServiceBaseUrl + "/api/member/get/{rNumber}", rNumber)
                .retrieve()
                .bodyToMono(MemberDto.class)
                .block();

        if (member != null) {
            Optional<Task> optionalTask = Optional.ofNullable(taskRepository.findTaskByTaskCode(taskCode));
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();
                task.setName(taskRequest.getName());
                task.setStatus(taskRequest.getStatus());
                task.setDueDate(taskRequest.getDueDate());
                task.setDescription(taskRequest.getDescription());
                task.setRNumber(rNumber); // Ensure the rNumber is updated if necessary

                taskRepository.save(task);
            } else {
                throw new TaskNotFoundException("Task not found for taskCode: " + taskCode);
            }
        } else {
            throw new UserNotFoundException("Member not found for r_number: " + rNumber);
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
    public MemberTaskResponse getMemberWithTask(String taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isPresent()){
            Task taskData = task.get();
            MemberDto member = getMemberByrNumber(taskData.getRNumber());

            if (member != null){
                return MemberTaskResponse.builder()
                        .id(taskData.getId())
                        .taskCode(taskData.getTaskCode())
                        .name(taskData.getName())
                        .description(taskData.getDescription())
                        .creationDate(taskData.getCreationDate())
                        .dueDate(taskData.getDueDate())
                        .status(taskData.getStatus())
                        .rNumber(member.getRNumber())
                        .firstName(member.getFirstName())
                        .lastName(member.getLastName())
                        .username(member.getUsername())
                        .email(member.getEmail())
                        .password(member.getPassword())
                        .taskCodes(member.getTaskCodes())
                        .build();
            }
        }
        return null;
    }


    public MemberDto getMemberByrNumber(String rNumber) {
        return webClient.get()
                .uri("http://" + teamServiceBaseUrl + "/api/member/get/{rNumber}", rNumber)
                .retrieve()
                .bodyToMono(MemberDto.class)
                .block();
    }
}
