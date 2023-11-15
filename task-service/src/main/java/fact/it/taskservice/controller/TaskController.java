package fact.it.taskservice.controller;

import fact.it.taskservice.dto.TaskDto;
import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
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

    @PostMapping("/add")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest taskRequest) {
        String taskName = taskRequest.getName();
        taskService.createTask(taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task '" + taskName + "' created successfully!");
    }

    @GetMapping("/all")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getAllTasksByTaskCode(@RequestParam List<UUID> taskCode){
        return taskService.getAllTasksByTaskCode(taskCode);
    }
    @GetMapping("/{taskCode}")
    public TaskResponse findTaskByTaskCode(@PathVariable UUID taskCode) {
        return taskService.findTaskByTaskCode(taskCode);
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable String taskId, @RequestBody TaskRequest taskRequest) {
        try {
            taskService.updateTask(taskId, taskRequest);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable String taskId) {
        try {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
