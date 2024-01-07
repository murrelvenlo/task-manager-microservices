package fact.it.assignmentservice.repository;

import fact.it.assignmentservice.model.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    TaskAssignment findByAssignmentCode(String assignmentCode);
    List<TaskAssignment> findByrNumberOrTaskCodeOrAssignmentCode(String rNumber, String taskCode, String assignmentCode);
    void deleteByAssignmentCode(String assignmentCode);
}
