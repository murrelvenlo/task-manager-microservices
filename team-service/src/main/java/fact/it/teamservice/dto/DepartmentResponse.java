package fact.it.teamservice.dto;

import fact.it.teamservice.model.Department;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String name;
    private String depCode;
}
