package fact.it.taskservice.repository;

import fact.it.taskservice.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByTaskCodeIn(List<String> taskCode);
    Task findTaskByTaskCode(String taskCode);
    Task findTaskByUserCode(String userCode);
    List<Task> findAllByUserCode(String userCode);
    List<Task> findByDueDateBetween(Date startDate, Date endDate);
    List<Task> findByCreationDateBetween(Date startDate, Date endDate);
}
