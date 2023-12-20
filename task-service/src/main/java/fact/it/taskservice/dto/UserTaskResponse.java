package fact.it.taskservice.dto;

import fact.it.taskservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskResponse {
    private String id;
    private String taskCode;
    private String name;
    private String description;
    private Date creationDate;
    private Date dueDate;
    private boolean isProfessional;
    private TaskStatus status;
    private String userCode;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private List<String> taskCodes;
}
