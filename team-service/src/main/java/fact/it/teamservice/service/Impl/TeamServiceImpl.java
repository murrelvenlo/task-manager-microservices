package fact.it.teamservice.service.Impl;

import fact.it.teamservice.dto.*;
import fact.it.teamservice.exception.DuplicateEntityException;
import fact.it.teamservice.exception.EntityNotFoundException;
import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.model.Team;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.repository.TeamRepository;
import fact.it.teamservice.service.TeamService;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {


    // List of predefined teacher names
    private final List<String> teacherNames = Arrays.asList("Jeroen", "Henk", "Kristine");
    // Map to store counters for each first letter
    private static final Map<Character, Integer> teamNumberCounters = new HashMap<>();
    private final ModelMapper modelMapper;
    private final WebClient webClient;


    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    @Override
    @Transactional
    public Team createTeam(TeamRequest teamRequest) {
        // Generate 10 random digits
        StringBuilder randomDigits = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            randomDigits.append(random.nextInt(10));
        }

        // Construct the team number
        String teamNumber = "team-" + randomDigits.toString();

        // Check if a team with the same name already exists
        if (teamRepository.existsByName(teamRequest.getName())) {
            throw new DuplicateEntityException("Team", "Team with the same name, " + teamRequest.getName() + ", already exists");
        }

        // Create new team
        Team newTeam = Team.builder()
                .teamNumber(teamNumber)
                .name(teamRequest.getName())
                .build();

        teamRepository.save(newTeam);

        return newTeam;
    }

    // Helper method to get random members from the list based on the count
    private List<Member> getRandomMembers(List<Member> members, int count) {
        Collections.shuffle(members); // Shuffle the list of members

        return members.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamOnlyResponse> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(team -> modelMapper.map(team, TeamOnlyResponse.class))
                .collect(Collectors.toList());
    }
    @Override
    public TeamResponse findTeamByNumber(String teamNumber) {
        Team team = teamRepository.findByTeamNumber(teamNumber);
        return modelMapper.map(team, TeamResponse.class);
    }

    @Override
    public void updateTeam(String teamNumber, TeamRequest teamRequest) {
        Team team = teamRepository.findByTeamNumber(teamNumber);
        if (team != null) {
            // Use ModelMapper to map properties from TeamRequest to Team
            modelMapper.map(teamRequest, team);
            teamRepository.save(team);
        } else {
            // Handle the case where the team is not found
            throw new RuntimeException("Team with teamNumber " + teamNumber + " not found");
        }
    }

    @Override
    @Transactional
    public void deleteTeamByNumber(String teamNumber) {
        Team team = teamRepository.findByTeamNumber(teamNumber);
        if (team != null) {
            teamRepository.delete(team);
        } else {
            // Handle the case where the team is not found
            throw new RuntimeException("Team with teamNumber " + teamNumber + " not found");
        }
    }

    @Override
    @Transactional
    public void assignMembersToTeam(String teamNumber, List<MemberRequest> memberRequests) {
        // Find the team by teamNumber
        Team team = teamRepository.findByTeamNumber(teamNumber);
        if (team == null) {
            throw new EntityNotFoundException("Team", "Team with number " + teamNumber + " not found");
        }

        // Iterate over member requests and associate them with the team
        for (MemberRequest memberRequest : memberRequests) {
            // Find the department by name
            Department department = departmentRepository.findByName(memberRequest.getDepName());
            if (department == null) {
                throw new EntityNotFoundException("Department", "Department with name " + memberRequest.getDepName() + " not found");
            }

            // Find the existing members from the database
            List<Member> existingMembers = memberRepository.findByrNumberAndDepartment(memberRequest.getRNumber(), department);

            if (existingMembers.isEmpty()) {
                throw new EntityNotFoundException("Member", "Member with RNumber " + memberRequest.getRNumber() + " not found in department " + department.getName());
            }

            if (existingMembers.size() > 1) {
                throw new IllegalStateException("Multiple members found with RNumber " + memberRequest.getRNumber() + " in department " + department.getName());
            }

            Member existingMember = existingMembers.get(0);

            // Associate the existing member with the team
            existingMember.setTeam(team);

            // Add the member to the team
            team.getMembers().add(existingMember);
        }

        // Save the updated team
        teamRepository.save(team);
    }

    @Override
    public void addMembersToTeam(Long teamId, List<MemberToTeamRequest> memberToTeamRequest) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Teaam not found with id: " + teamId));

        List<String> rNumbers = memberToTeamRequest.stream()
                .map(MemberToTeamRequest::getRNumber)
                .collect(Collectors.toList());

        List<Member> membersToAdd = memberRepository.findByrNumberIn(rNumbers);

        // found all members or not
        if (membersToAdd.size() != rNumbers.size()) {
            throw new EntityNotFoundException("One or more members not found");

        }
        // update team's member
        team.getMembers().addAll(membersToAdd);

        // update each member's team
        membersToAdd.forEach(member -> member.setTeam(team));

        // save changes
        teamRepository.save(team);
    }

    private String getRandomTeacherName() {
        // Use Random to select a random index from the teacherNames list
        Random random = new Random();
        int randomIndex = random.nextInt(teacherNames.size());
        return teacherNames.get(randomIndex);
    }

//    private void sendTeamAddedEmail(MemberRequest memberRequest) {
//        // Create a MailDto with user information
//        MailDto mailDto = MailDto.builder()
//                .recipient(memberRequest.getEmail())
//                .messageSubject("Assigned to team")
//                .messageBody("Dear " + memberRequest.getFirstName() + ",\nYou are added to a team")
//                .build();
//
//        // Send the email using WebClient to the mail-service
//        webClient.post()
//                .uri("http://" + emailServiceBaseUrl + "/api/email/send-email")
//                .bodyValue(mailDto)
//                .retrieve()
//                .toBodilessEntity()
//                .block();
//    }
}
