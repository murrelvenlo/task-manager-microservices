package fact.it.taskservice.dto;

import fact.it.taskservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest
{
    private UUID taskCode;
    private String name;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private UUID userCode;
    private boolean isProfessional;
    private TaskStatus status;
    private String email;
}
