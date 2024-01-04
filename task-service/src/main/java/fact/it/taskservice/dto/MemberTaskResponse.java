package fact.it.taskservice.dto;

import fact.it.taskservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberTaskResponse {
    private String id;
    private String taskCode;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private Date dueDate;
    private TaskStatus status;
    private String rNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private List<String> taskCodes;
}
