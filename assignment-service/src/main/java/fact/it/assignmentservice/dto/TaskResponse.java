package fact.it.assignmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private String taskCode;
    private String name;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private String rNumber;
    private TaskStatusDTO status;
}
