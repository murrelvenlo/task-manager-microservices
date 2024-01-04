package fact.it.teamservice.service.Impl;

import fact.it.teamservice.dto.MailDto;
import fact.it.teamservice.dto.MemberResponse;
import fact.it.teamservice.dto.TaskDTO;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.repository.TeamRepository;
import fact.it.teamservice.service.MemberTaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberTaskServiceImpl implements MemberTaskService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(MemberTaskServiceImpl.class);


    @Override
    public void addTaskForMember(String rNumber, TaskDTO taskDto) {
        Member member = memberRepository.findByrNumber(rNumber);

        if (member != null) {
            // Set the userCode on the taskDto before making the request
            taskDto.setRNumber(rNumber);

            try {
                webClient.post()
                        .uri("http://localhost:8080/api/tasks/add")
                        .bodyValue(taskDto)
                        .retrieve()
                        .toBodilessEntity()
                        .block(); // Block to wait for the result, handle this differently in a real application

                // Log the information or take any other action
                log.info("Task added successfully for member {}", rNumber);
                sendUsTaskCreationEmail(member);


            } catch (Exception e) {
                // Handle exceptions appropriately
                log.error("Exception adding task for member: {}", e.getMessage());
                throw new RuntimeException("Exception adding task for member: " + e.getMessage());
            }
        }
    }

    @Override
    public void updateTaskForMember(String taskCode, TaskDTO taskDto) {
        Member member = memberRepository.findByrNumber(taskDto.getRNumber());

        if (member != null) {
            // Set the userCode on the taskRequest before making the request
            taskDto.setRNumber(member.getRNumber());

            try {
                webClient.put()
                        .uri("http://localhost:8080/api/tasks/update/{taskCode}", taskCode)
                        .bodyValue(taskDto)
                        .retrieve()
                        .toBodilessEntity()
                        .block(); // Block to wait for the result, handle this differently in a real application

                // Log the information or take any other action
                String fullName = Objects.toString(member.getFirstName(), "") + " " + Objects.toString(member.getLastName(), "");

                log.info("Task updated successfully for member {}", fullName);
            } catch (Exception e) {
                // Handle exceptions appropriately
                log.error("Exception updating task for member: {}", e.getMessage());
                throw new RuntimeException("Exception updating task for member: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Member not found for member: " + member.getRNumber());
        }

    }

    @Override
    public void deleteTaskForMember(String rNumber, String taskCode) {
        Member member = memberRepository.findByrNumber(rNumber);

        if (member != null) {
            try {
                webClient.delete()
                        .uri("http://localhost:8080/api/tasks/delete/{taskCode}", taskCode)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
            } catch (Exception e) {

                // Handle exceptions appropriately
                log.error("Exception deleting task for member: {}", e.getMessage());
                throw new RuntimeException("Exception updating task for member: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Member not found for r_number: " + member.getRNumber());
        }
    }

    @Override
    public List<TaskDTO> getTasksForMemberByrNumber(String rNumber) {
        try {
            // Get the rNumber
            Member member = memberRepository.findByrNumber(rNumber);

            if (member != null) {
                // Make a request to the task-service to get tasks by rNumber
                List<TaskDTO> userTasks = webClient
                        .get()
                        .uri("http://localhost:8080/api/tasks/getByrNumber/{rNumber}", rNumber)
                        .retrieve()
                        .bodyToFlux(TaskDTO.class)
                        .collectList()
                        .block();

                // Filter tasks based on the provided userCode
                return userTasks.stream()
                        .filter(task -> rNumber.equals(task.getRNumber()))
                        .collect(Collectors.toList());
            } else {
                // Handle the case where the current member is not found
                throw new RuntimeException("Current member not found");
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            throw new RuntimeException("Error retrieving tasks for member " + rNumber, e);
        }
    }

    @Override
    public void senTaskReminder(TaskDTO taskDto, MemberResponse memberResponse) {
        Member member = memberRepository.findByrNumber(taskDto.getRNumber());


        // Check if the due date is today or within the next 3 days
        LocalDate dueDate = taskDto.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        if (dueDate.isEqual(today)) {
            // Create a MailDto with user information
            MailDto mailDto = MailDto.builder()
                    .recipient(memberResponse.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + memberResponse.getFirstName() + ", " + taskDto.getName() + ",\nYour task is due to today.")
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
                    .recipient(memberResponse.getEmail())
                    .messageSubject("Task Created")
                    .messageBody("Dear " + memberResponse.getFirstName()  + ", " + taskDto.getName() + ",\nYour task is due in three days.")
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

    private void sendUsTaskCreationEmail(Member member) {
        // Create a MailDto with user information
        MailDto mailDto = MailDto.builder()
                .recipient(member.getEmail())
                .messageSubject("Task Created")
                .messageBody("Dear " + member.getFirstName() + ",\nYour task has been successfully created.")
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
