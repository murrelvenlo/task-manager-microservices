package fact.it.userservice.scheduler;

import fact.it.userservice.dto.TaskDTO;
import fact.it.userservice.dto.UserResponse;
import fact.it.userservice.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskReminderScheduler {

    @Autowired
    private UserTaskService userTaskService;

    // This method will be automatically scheduled to run at a specified interval
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendTaskReminders(){
        List<TaskDTO> tasks = userTaskService.getAllTask();
        for (TaskDTO task : tasks) {
            UserResponse userResponse = (UserResponse) userTaskService.getTasksForCurrentUserByUserCode(task.getUserCode());
            userTaskService.senTaskReminder(task, userResponse);
        }
    }
}
