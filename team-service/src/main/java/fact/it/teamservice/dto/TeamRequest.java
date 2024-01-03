package fact.it.teamservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TeamRequest {
    private String teamNumber;
    private String name;
    private List<MemberResponse> members = new ArrayList<>();
}
