package fact.it.teamservice.service;

import fact.it.teamservice.dto.MemberResponse;
import fact.it.teamservice.dto.TaskDTO;

import java.util.List;

public interface MemberTaskService {
    void addTaskForMember(String userCode, TaskDTO taskDto);
    void updateTaskForMember(String taskCode, TaskDTO taskDto);
    void deleteTaskForMember(String userCode, String taskCode);
    List<TaskDTO> getTasksForMemberByrNumber(String rNumber);
    void senTaskReminder(TaskDTO taskDto, MemberResponse memberResponse);
    public List<TaskDTO> getAllTask();
}
