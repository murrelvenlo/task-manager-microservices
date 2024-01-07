package fact.it.assignmentservice.service;

import fact.it.assignmentservice.dto.AssignmentRequest;
import fact.it.assignmentservice.dto.AssignmentResponse;
import fact.it.assignmentservice.dto.TaskResponse;

import java.util.List;

public interface TaskAssignmentService {
    void createAssignment(AssignmentRequest assignmentRequest);
    List<AssignmentResponse> getAssignmentsByRNumberOrTaskCode(String rNumber, String taskCode, String assignmentCode);
    List<AssignmentResponse> getAllAssignments();
    void updateAssignment(String assignmentCode, AssignmentRequest assignmentRequest);
    void deleteAssignment(String assignmentCode);
}
