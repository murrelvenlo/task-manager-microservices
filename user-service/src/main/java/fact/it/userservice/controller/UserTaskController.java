package fact.it.userservice.controller;


import fact.it.userservice.dto.TaskDTO;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.model.UserEntity;
import fact.it.userservice.repository.UserRepository;
import fact.it.userservice.service.UserService;
import fact.it.userservice.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user-task")
@RequiredArgsConstructor
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;
    private final UserRepository userRepository;

    @PostMapping("/addForCurrentUser/{userCode}")
    public ResponseEntity<String> addTaskForCurrentUser(@RequestBody TaskDTO taskDto,
            @PathVariable String userCode) {
        try {
            userTaskService.addTaskForUser(userCode, taskDto);
            return ResponseEntity.ok("Task, " + taskDto.getName() + ", created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the task for the current user");
        }
    }

    @PutMapping("/update/{userCode}/{taskCode}")
    public ResponseEntity<String> updateTaskForUser(
            @PathVariable String taskCode,
            @PathVariable String userCode,
            @RequestBody TaskDTO taskDto) {
        try {
            UserEntity user = userRepository.findByUserCode(userCode);
            if (user != null) {
                String fullName = Objects.toString(user.getFirstName(), "") + " " + Objects.toString(user.getLastName(), "");
                userTaskService.updateTaskForUser(taskCode, taskDto);
                return ResponseEntity.ok("Task updated successfully for user, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for code " + user.getUserCode());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userCode}/{taskCode}")
    public ResponseEntity<String> deleteTaskForUser(
            @PathVariable String taskCode,
            @PathVariable String userCode) {
        try {
            UserEntity user = userRepository.findByUserCode(userCode);
            if (user != null) {
                String fullName = Objects.toString(user.getFirstName(), "") + " " + Objects.toString(user.getLastName(), "");
                userTaskService.deleteTaskForUser(userCode, taskCode);
                return ResponseEntity.ok("Task deleted successfully for user, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for code " + user.getUserCode());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getByUserCode/{userCode}")
    public ResponseEntity<List<TaskDTO>> getAllTasksByUserCode(@PathVariable String userCode) {
        List<TaskDTO> tasks = userTaskService.getTasksForCurrentUserByUserCode(userCode);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    @GetMapping("/getAllTasksForCurrentUser/{userCode}")
    public ResponseEntity<List<TaskDTO>> getAllTasksForCurrentUser(@PathVariable String userCode) {
        try {
            UserEntity user = userRepository.findByUserCode(userCode);
            List<TaskDTO> tasks = userTaskService.getTasksForCurrentUserByUserCode(user.getUserCode());

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

