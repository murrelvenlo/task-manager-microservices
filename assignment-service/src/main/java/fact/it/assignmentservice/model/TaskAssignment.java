package fact.it.assignmentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_assignments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assignmentCode;
    private String taskCode;
    private String rNumber;
    private boolean completed;
    private LocalDateTime assignmentDate;
    private LocalDateTime deadline;
    private String notes;
    private TaskAssignmentStatus status;
}
