package fact.it.teamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class DepartmentRequest {
    private String name;
    private String depCode;
//    private List<Member> members = new ArrayList<>();
}
