package fact.it.taskservice.dto;

import fact.it.taskservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto
{
    private String id;
    private String taskCode;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private Date dueDate;
    private String rNumber;
    private TaskStatus status;
}
