package fact.it.taskservice.service.Impl;

import fact.it.taskservice.dto.TaskDto;
import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper mapper;
    private final MongoTemplate mongoTemplate;
    @Override
    public void createTask(TaskRequest taskRequest) {
        Task task = Task.builder()
                .taskCode(UUID.randomUUID())
                .name(taskRequest.getName())
                .status(taskRequest.getStatus())
                .creationDate(new Date())
                .dueDate(new Date())
                .description(taskRequest.getDescription())
                .isProfessional(taskRequest.isProfessional())
                .build();

        taskRepository.save(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getAllTasksByTaskCode(List<UUID> taskCode) {
        List<Task> tasks = taskRepository.findByTaskCodeIn(taskCode);

        return tasks.stream().map(task -> mapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse findTaskByTaskCode(UUID taskCode) {
        Task task = taskRepository.findTaskByTaskCode(taskCode);
        if (task != null){
            return mapper.map(task, TaskResponse.class);
        }
        return null;
    }


    @Override
    public void updateTask(String taskId, TaskRequest taskRequest) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()){
            Task task = taskOptional.get();

            if(taskRequest.getName() != null && !taskRequest.getName().isEmpty()) {
                task.setName(taskRequest.getName());
            }
            if(taskRequest.getDescription() != null && !taskRequest.getDescription().isEmpty()) {
                task.setDescription(taskRequest.getDescription());
            }
            if(taskRequest.getStatus() != null) {
                task.setStatus(taskRequest.getStatus());
            }
            if(taskRequest.getDueDate() != null) {
                task.setDueDate(taskRequest.getDueDate());
            }
            task.setProfessional(taskRequest.isProfessional()); // Assuming isProfessional is not null

            taskRepository.save(task);
        }
        else {
            throw new RuntimeException("Task not found with task id: " + taskId);
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

}
