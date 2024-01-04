package fact.it.teamservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String rNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String taskCode;
    private DepartmentResponse department;
    private TeamResponse team;
}
