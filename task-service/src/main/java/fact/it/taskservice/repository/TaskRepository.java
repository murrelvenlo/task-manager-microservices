package fact.it.taskservice.repository;

import fact.it.taskservice.model.Task;
import fact.it.taskservice.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByTaskCodeIn(List<String> taskCode);
    Task findTaskByTaskCode(String taskCode);
    Task findTaskByrNumber(String rNumber);
    List<Task> findAllByrNumber(String rNumber);
    List<Task> findByDueDateBetween(Date startDate, Date endDate);
    List<Task> findByCreationDateBetween(Date startDate, Date endDate);
    boolean existsByNameAndStatus(String name, TaskStatus status);
    void deleteByTaskCode(String taskCode);
}
