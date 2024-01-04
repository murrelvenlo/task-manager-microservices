package fact.it.teamservice.controller;

import fact.it.teamservice.dto.TaskDTO;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.service.MemberService;
import fact.it.teamservice.service.MemberTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/member-task")
@RequiredArgsConstructor
public class MemberTaskController {

    private final MemberService memberService;
    private final MemberTaskService memberTaskService;
    private final MemberRepository memberRepository;

    @PostMapping("/add-for-member/{rNumber}")
    public ResponseEntity<String> addTaskForMember(@RequestBody TaskDTO taskDto,
                                                        @PathVariable String rNumber) {
        try {
            memberTaskService.addTaskForMember(rNumber, taskDto);
            return ResponseEntity.ok("Task, " + taskDto.getName() + ", created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the task for the current user");
        }
    }

    @PutMapping("/update/{rNumber}/{taskCode}")
    public ResponseEntity<String> updateTaskForUser(
            @PathVariable String taskCode,
            @PathVariable String rNumber,
            @RequestBody TaskDTO taskDto) {
        try {
            Member member = memberRepository.findByrNumber(rNumber);
            if (member != null) {
                String fullName = Objects.toString(member.getFirstName(), "") + " " + Objects.toString(member.getLastName(), "");
                memberTaskService.updateTaskForMember(taskCode, taskDto);
                return ResponseEntity.ok("Task updated successfully for member, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found for r_number " + member.getRNumber());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{rNumber}/{taskCode}")
    public ResponseEntity<String> deleteTaskForUser(
            @PathVariable String taskCode,
            @PathVariable String rNumber) {
        try {
            Member member = memberRepository.findByrNumber(rNumber);
            if (member != null) {
                String fullName = Objects.toString(member.getFirstName(), "") + " " + Objects.toString(member.getLastName(), "");
                memberTaskService.deleteTaskForMember(rNumber, taskCode);
                return ResponseEntity.ok("Task deleted successfully for member, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found for r_number " + member.getRNumber());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/get-by-rNumber/{rNumber}")
    public ResponseEntity<List<TaskDTO>> getAllTasksByrNumber(@PathVariable String rNumber) {
        List<TaskDTO> tasks = memberTaskService.getTasksForMemberByrNumber(rNumber);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    @GetMapping("/get-all-tasks-for-member/{rNumber}")
    public ResponseEntity<List<TaskDTO>> getAllTasksForMember(@PathVariable String rNumber) {
        try {
            Member member = memberRepository.findByrNumber(rNumber);
            List<TaskDTO> tasks = memberTaskService.getTasksForMemberByrNumber(member.getRNumber());

            if (tasks.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(tasks);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
