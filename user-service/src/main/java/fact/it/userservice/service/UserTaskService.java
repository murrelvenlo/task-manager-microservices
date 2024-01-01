package fact.it.userservice.service;

import fact.it.userservice.dto.TaskDTO;
import fact.it.userservice.dto.UserResponse;

import java.util.List;

public interface UserTaskService {
    void addTaskForUser(String userCode, TaskDTO taskDto);
    void updateTaskForUser(String taskCode, TaskDTO taskDto);
    void deleteTaskForUser(String userCode, String taskCode);
    List<TaskDTO> getTasksForCurrentUserByUserCode(String userCode);
    void senTaskReminder(TaskDTO taskDto, UserResponse user);
    public List<TaskDTO> getAllTask();
}
