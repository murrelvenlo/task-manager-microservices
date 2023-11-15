package fact.it.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private String id;
    private UUID taskCode;
    private String name;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private boolean isProfessional;
    private String userCode;
    private TaskStatus status;
}
