package fact.it.taskservice.service;

import fact.it.taskservice.dto.TaskDto;
import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService
{
    void createTask(TaskRequest taskRequest);
    List<TaskResponse> getAllTasks();
    List<TaskResponse> getAllTasksByTaskCode(List<String> taskCode);
    TaskResponse findTaskByTaskCode(String taskCode);
    void updateTask(String taskId, TaskRequest taskRequest);
    void deleteTask(String taskId);
    void deleteTaskByCode(String taskCode);
    void updateTaskByCode(String taskCode, TaskRequest taskRequest);
    List<TaskResponse> getAllTasksByUserCode(String userCode);
}
