package fact.it.taskservice.controller;

import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.dto.UserDto;
import fact.it.taskservice.dto.UserTaskResponse;
import fact.it.taskservice.exception.TaskNotFoundException;
import fact.it.taskservice.exception.UserNotFoundException;
import fact.it.taskservice.service.TaskUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskUserController {

    private final TaskUserService taskUserService;
    @PostMapping("/add/{userCode}")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest taskRequest, @PathVariable String userCode) {
        try {
            taskUserService.createTask(taskRequest, userCode);
            return ResponseEntity.ok("Task, " + taskRequest.getName() + ", created successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the task");
        }
    }
    @GetMapping("/all")
    public List<TaskResponse> getAllTasks() {
        return taskUserService.getAllTasks();
    }

    @PutMapping("/update/{taskCode}/{userCode}")
    public ResponseEntity<String> updateTask(@PathVariable String taskCode, @RequestBody TaskRequest taskRequest, @PathVariable String userCode) {
        try {
            taskUserService.updateTask(taskCode, taskRequest, userCode);
            return ResponseEntity.ok("Task, " + taskRequest.getName() + ", updated successfully");
        } catch (TaskNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the task");
        }
    }


    @GetMapping("/user/{userCode}")
    public ResponseEntity<UserDto> getUserByCode(@PathVariable String userCode) {
        UserDto user = taskUserService.getUserByCode(userCode);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String taskId) {
        try {
            taskUserService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{taskId}")
    public UserTaskResponse getUserTask(@PathVariable String taskId) {
        return taskUserService.getUserWithTask(taskId);
    }
}
