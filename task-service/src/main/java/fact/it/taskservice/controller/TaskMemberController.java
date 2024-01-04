package fact.it.taskservice.controller;

import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.dto.MemberDto;
import fact.it.taskservice.dto.MemberTaskResponse;
import fact.it.taskservice.exception.TaskNotFoundException;
import fact.it.taskservice.exception.UserNotFoundException;
import fact.it.taskservice.service.TaskMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskMemberController {

    private final TaskMemberService taskMemberService;
    @PostMapping("/add/{rNumber}")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest taskRequest, @PathVariable String rNumber) {
        try {
            taskMemberService.createTask(taskRequest, rNumber);
            return ResponseEntity.ok("Task, " + taskRequest.getName() + ", created successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the task");
        }
    }
    @GetMapping("/all")
    public List<TaskResponse> getAllTasks() {
        return taskMemberService.getAllTasks();
    }

    @PutMapping("/update/{taskCode}/{rNumber}")
    public ResponseEntity<String> updateTask(@PathVariable String taskCode, @RequestBody TaskRequest taskRequest, @PathVariable String rNumber) {
        try {
            taskMemberService.updateTask(taskCode, taskRequest, rNumber);
            return ResponseEntity.ok("Task, " + taskRequest.getName() + ", updated successfully");
        } catch (TaskNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the task");
        }
    }


    @GetMapping("/member/{rNumber}")
    public ResponseEntity<MemberDto> getMemberByrNumber(@PathVariable String rNumber) {
        MemberDto member = taskMemberService.getMemberByrNumber(rNumber);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String taskId) {
        try {
            taskMemberService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{taskId}")
    public MemberTaskResponse getMemberTask(@PathVariable String taskId) {
        return taskMemberService.getMemberWithTask(taskId);
    }
}
