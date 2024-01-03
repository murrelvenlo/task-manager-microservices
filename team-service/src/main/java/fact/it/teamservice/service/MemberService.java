package fact.it.teamservice.service;

import fact.it.teamservice.dto.DepartmentRequest;
import fact.it.teamservice.dto.DepartmentResponse;
import fact.it.teamservice.dto.MemberRequest;
import fact.it.teamservice.dto.MemberResponse;
import fact.it.teamservice.model.Member;

import java.util.List;

public interface MemberService {
    Member addMember(MemberRequest memberRequest);
    List<MemberResponse> getAllMembers();
    MemberResponse findMemberByrNumber(String rNumber);
    void updateMember(String rNumber, MemberRequest request);
    void deleteByrNumber(String rNumber);
    void addMemberToTeam(String teamNumber, String rNumber);
}
