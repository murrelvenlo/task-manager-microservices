package fact.it.assignmentservice.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "taskAssignments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskAssignment {
    @Id
    private String id;
    private String assignmentCode;
    private String taskCode;
    private String rNumber;
    private boolean completed;
    private LocalDateTime assignmentDate;
    private LocalDateTime deadline;
    private String notes;
    private TaskAssignmentStatus status;
}
