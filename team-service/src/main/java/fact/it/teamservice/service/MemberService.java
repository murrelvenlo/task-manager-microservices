package fact.it.teamservice.service;

import fact.it.teamservice.dto.*;
import fact.it.teamservice.model.Member;

import java.util.List;

public interface MemberService {
    Member addMember(MemberRequest memberRequest);
    List<MemberResponse> getAllMembers();
    MemberResponse findMemberByrNumber(String rNumber);
    void updateMember(String rNumber, UpdateMemberRequest request);
    void deleteByrNumber(String rNumber);
    void addMemberToTeam(String teamNumber, String rNumber);
    void removeMemberFromTeam(Long teamId, String rNumber);
}
