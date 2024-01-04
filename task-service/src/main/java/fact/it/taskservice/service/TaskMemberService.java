package fact.it.taskservice.service;

import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.dto.MemberDto;
import fact.it.taskservice.dto.MemberTaskResponse;

import java.util.List;

public interface TaskMemberService
{
    void createTask(TaskRequest taskRequest, String rNumber);
    List<TaskResponse> getAllTasks();
    void sendTaskCreationEmail(TaskRequest taskRequest, String rNumber);
    MemberDto getMemberByrNumber(String rNumber);
    void updateTask(String taskCode, TaskRequest taskRequest, String rNumber);
    void deleteTask(String taskId);
    MemberTaskResponse getMemberWithTask(String taskId);
}
