package fact.it.teamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateMemberRequest {
    private String rNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String taskCode;
    private DepartmentResponse department;
    private TeamResponse team;
}
