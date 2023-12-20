package fact.it.userservice.service;

import fact.it.userservice.dto.TaskDTO;

import java.util.List;

public interface UserTaskService {
    void addTaskForUser(TaskDTO taskDto);
    void updateTaskForUser(String taskCode, TaskDTO taskDto);
    void deleteTaskForUser(String taskCode);
    List<TaskDTO> getTasksForCurrentUserByUserCode(String userCode);
}
