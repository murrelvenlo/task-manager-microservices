package fact.it.userservice.controller;

import fact.it.userservice.dto.TaskDTO;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.service.AuthService;
import fact.it.userservice.service.UserService;
import fact.it.userservice.service.UserTaskService;
import lombok.RequiredArgsConstructor;
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

    private final UserTaskService userTaskService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/addForCurrentUser")
    public ResponseEntity<String> addTaskForCurrentUser(@RequestBody TaskDTO taskDto) {
        try {
            userTaskService.addTaskForUser(taskDto);
            return ResponseEntity.ok("Task, " + taskDto.getName() + ", created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding the task for the current user");
        }
    }


    @PutMapping("/update/{taskCode}")
    public ResponseEntity<String> updateTaskForUser(
            @PathVariable String taskCode,
            @RequestBody TaskDTO taskDto) {
        try {
            UserResponse currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                String fullName = Objects.toString(currentUser.getFirstName(), "") + " " + Objects.toString(currentUser.getLastName(), "");
                userTaskService.updateTaskForUser(taskCode, taskDto);
                return ResponseEntity.ok("Task updated successfully for user, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for code " + currentUser.getUserCode());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{taskCode}")
    public ResponseEntity<String> deleteTaskForUser(
            @PathVariable String taskCode) {
        try {
            UserResponse currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                String fullName = Objects.toString(currentUser.getFirstName(), "") + " " + Objects.toString(currentUser.getLastName(), "");
                userTaskService.deleteTaskForUser(taskCode);
                return ResponseEntity.ok("Task deleted successfully for user, " + fullName);
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for code " + currentUser.getUserCode());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getByUserCode/{userCode}")
    public ResponseEntity<List<TaskDTO>> getAllTasksByUserCode(@PathVariable String userCode) {
        List<TaskDTO> tasks = userTaskService.getTasksForCurrentUserByUserCode(userCode);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build(); // or ResponseEntity.ok(Collections.emptyList());
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    @GetMapping("/getAllTasksForCurrentUser")
    public ResponseEntity<List<TaskDTO>> getAllTasksForCurrentUser() {
        try {
            UserResponse currentUser = authService.getCurrentUser();

            if (currentUser != null) {
                List<TaskDTO> tasks = userTaskService.getTasksForCurrentUserByUserCode(currentUser.getUserCode());

                if (tasks.isEmpty()) {
                    return ResponseEntity.noContent().build(); // or ResponseEntity.ok(Collections.emptyList());
                } else {
                    return ResponseEntity.ok(tasks);
                }
            } else {
                // Handle the case where user is not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
