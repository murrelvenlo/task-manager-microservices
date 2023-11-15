package fact.it.taskservice.repository;

import fact.it.taskservice.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByTaskCodeIn(List<UUID> taskCode);
    Task findTaskByTaskCode(UUID taskCode);
}
