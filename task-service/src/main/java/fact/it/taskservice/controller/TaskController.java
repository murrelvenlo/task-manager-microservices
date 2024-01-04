package fact.it.taskservice.controller;

import fact.it.taskservice.dto.TaskDto;
import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.exception.DuplicateEntityException;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @PostMapping("/add")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest taskRequest) {
        try {
            taskService.createTask(taskRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Task '" + taskRequest.getName() + "' created successfully!");
        } catch (DuplicateEntityException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error creating task: " + ex.getMessage());
        }
    }


    @GetMapping("/get/all")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getAllTasksByTaskCode(@RequestParam List<String> taskCode){
        return taskService.getAllTasksByTaskCode(taskCode);
    }
    @GetMapping("/get/{taskCode}")
    public TaskResponse findTaskByTaskCode(@PathVariable String taskCode) {
        return taskService.findTaskByTaskCode(taskCode);
    }

    @PutMapping("/updatebyId/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable String taskId, @RequestBody TaskRequest taskRequest) {
        try {
            taskService.updateTask(taskId, taskRequest);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{taskCode}")
    public ResponseEntity<String> updateMemberTask(
            @PathVariable String taskCode,
            @RequestBody TaskRequest taskRequest) {
        try {
            taskService.updateTaskByCode(taskCode, taskRequest);
            return ResponseEntity.ok("Task updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating task");
        }
    }

    @DeleteMapping("/delete/{taskCode}")
    public ResponseEntity<String> deleteTask(@PathVariable String taskCode) {
        try {
            taskService.deleteTaskByCode(taskCode);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-by-rNumber/{rNumber}")
    public ResponseEntity<List<TaskResponse>> getAllTasksByrNumber(@PathVariable String rNumber) {
        List<TaskResponse> tasks = taskService.getAllTasksByrNumber(rNumber);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    @DeleteMapping("/deleteBy/{taskId}")
    public ResponseEntity<String> deleteTaskById(@PathVariable String taskId) {
        try {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
