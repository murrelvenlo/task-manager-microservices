package fact.it.taskservice.service;

import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.dto.UserDto;
import fact.it.taskservice.dto.UserTaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskUserService
{
    void createTask(TaskRequest taskRequest, String userCode);
    List<TaskResponse> getAllTasks();
    void sendTaskCreationEmail(TaskRequest taskRequest, UUID userCode);
    UserDto getUserByCode(String userCode);
    void updateTask(String taskCode, TaskRequest taskRequest, String userCode);
    void deleteTask(String taskId);
    UserTaskResponse getUserWithTask(String taskId);
}
