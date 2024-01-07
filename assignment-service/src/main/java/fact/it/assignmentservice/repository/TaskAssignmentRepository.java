package fact.it.assignmentservice.repository;

import fact.it.assignmentservice.model.TaskAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskAssignmentRepository extends MongoRepository<TaskAssignment, String> {
    TaskAssignment findByAssignmentCode(String assignmentCode);
    List<TaskAssignment> findByrNumberOrTaskCodeOrAssignmentCode(String rNumber, String taskCode, String assignmentCode);
    void deleteByAssignmentCode(String assignmentCode);
}
