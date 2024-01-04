package fact.it.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String rNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private List<String> taskCodes;
}
