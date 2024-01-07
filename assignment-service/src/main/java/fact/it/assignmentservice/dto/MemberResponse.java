package fact.it.assignmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String rNumber;
    private String taskCode;
    private String firstName;
    private String lastName;
    private String email;
}
