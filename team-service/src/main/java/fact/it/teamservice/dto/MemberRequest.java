package fact.it.teamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MemberRequest {
    private String rNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String taskCode;
//    private Long depId;
    private String depName;
}
