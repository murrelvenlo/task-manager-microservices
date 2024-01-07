package fact.it.assignmentservice.dto;

import fact.it.assignmentservice.model.TaskAssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentRequest {
    private String assignmentCode;
    private String taskCode;
    private String rNumber;
    private boolean completed;
    private LocalDateTime deadline;
    private String notes;
    private TaskAssignmentStatus status;
}
