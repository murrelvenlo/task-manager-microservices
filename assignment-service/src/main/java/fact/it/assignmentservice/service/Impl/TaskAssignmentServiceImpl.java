package fact.it.assignmentservice.service.Impl;

import fact.it.assignmentservice.dto.*;
import fact.it.assignmentservice.model.TaskAssignment;
import fact.it.assignmentservice.repository.TaskAssignmentRepository;
import fact.it.assignmentservice.service.TaskAssignmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAssignmentServiceImpl implements TaskAssignmentService {
    @Value("${emailservice.baseurl}")
    private String emailServiceBaseUrl;
    @Value("${teamservice.baseurl}")
    private String teamServiceBaseUrl;
    @Value("${taskservice.baseurl}")
    private String taskServiceBaseUrl;
    private final TaskAssignmentRepository assignmentRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;

    @Override
    public void createAssignment(AssignmentRequest assignmentRequest) {
        // Fetch task information from task-service
        TaskResponse task = getTaskByCode(assignmentRequest.getTaskCode());

        // Fetch member information from team-service
        MemberResponse member = getMemberByRNumber(assignmentRequest.getRNumber());

        // Generate assignment code
        String assignmentCode = generateAssignmentCode();
                // Create the assignment
        TaskAssignment assignment = TaskAssignment.builder()
                .assignmentCode(assignmentCode)
                .taskCode(task.getTaskCode())
                .rNumber(member.getRNumber())
                .assignmentDate(LocalDateTime.now())
                .deadline(assignmentRequest.getDeadline())
                .status(assignmentRequest.getStatus())
                .completed(assignmentRequest.isCompleted())
                .notes(assignmentRequest.getNotes())
                .build();


        assignmentRepository.save(assignment);
        sendUsTaskCreationEmail(member);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByRNumberOrTaskCode(String rNumber, String taskCode, String assignmentCode) {
        List<TaskAssignment> assignments = assignmentRepository.findByrNumberOrTaskCodeOrAssignmentCode(rNumber, taskCode, assignmentCode);
        return assignments.stream()
                .map(assignment -> mapper.map(assignment, AssignmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getAllAssignments() {
        List<TaskAssignment> assignments = assignmentRepository.findAll();
        return assignments.stream()
                .map(assignment -> mapper.map(assignment, AssignmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateAssignment(String assignmentCode, AssignmentRequest assignmentRequest) {
        // Fetch the existing assignment
        TaskAssignment existingAssignment = assignmentRepository.findByAssignmentCode(assignmentCode);
        if (existingAssignment == null) {
            throw new EntityNotFoundException("Assignment not found with code: " + assignmentCode);
        }

        // Update the assignment fields
        existingAssignment.setTaskCode(assignmentRequest.getTaskCode());
        existingAssignment.setRNumber(assignmentRequest.getRNumber());
        existingAssignment.setCompleted(assignmentRequest.isCompleted());
        existingAssignment.setDeadline(assignmentRequest.getDeadline());
        existingAssignment.setNotes(assignmentRequest.getNotes());
        existingAssignment.setStatus(assignmentRequest.getStatus());

        // Save the updated assignment back to the repository
        assignmentRepository.save(existingAssignment);

    }

    @Override
    @Transactional
    public void deleteAssignment(String assignmentCode) {
        // Fetch the existing assignment
        TaskAssignment existingAssignment = assignmentRepository.findByAssignmentCode(assignmentCode);
        if (existingAssignment != null){
            assignmentRepository.deleteByAssignmentCode(assignmentCode);
        } else {
            throw new EntityNotFoundException("Assignment not found with code: " + assignmentCode);
        }
    }

    private TaskResponse getTaskByCode(String taskCode) {
        return webClient.get()
                .uri("http://" + taskServiceBaseUrl + "/api/tasks/get/{taskCode}", taskCode)
                .retrieve()
                .bodyToMono(TaskResponse.class)
                .block();
    }

    private MemberResponse getMemberByRNumber(String rNumber) {
        return webClient.get()
                .uri("http://" + teamServiceBaseUrl + "/api/member/get/{rNumber}", rNumber)
                .retrieve()
                .bodyToMono(MemberResponse.class)
                .block();
    }

    public String generateAssignmentCode() {
        // Prefix for the assignment code
        String prefix = "asnmt-";

        // Generate 7 random numbers
        String randomNumbers = generateRandomNumbers(7);

        // Combine prefix and random numbers to create the assignment code
        return prefix + randomNumbers;
    }

    public String generateRandomNumbers(int length) {
      return new Random()
              .ints(length, 0, 10)  // Generate 'length' random numbers between 0 (inclusive) and 10 (exclusive)
              .mapToObj(Integer::toString) // Convert each int to a String
              .collect(Collectors.joining()); // Concatenate the Strings
    }

    public void sendUsTaskCreationEmail(MemberResponse member) {
        // Create a MailDto with user information
        MailDto mailDto = MailDto.builder()
                .recipient(member.getEmail())
                .messageSubject("Task Created")
                .messageBody("Dear " + member.getFirstName() + ",\nYou are successfully assigned to the task with " + member.getTaskCode())
                .build();

        // Send the email using WebClient to the mail-service
        webClient.post()
                .uri("http://" + emailServiceBaseUrl + "/api/email/send-email")
                .bodyValue(mailDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
