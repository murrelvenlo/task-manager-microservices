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
import jakarta.annotation.PostConstruct;
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
    public void updateTeam(String teamNumber, UpdateTeamRequest teamRequest) {
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
    public void deleteTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()){
            teamRepository.deleteById(teamId);
        } else {
            throw new RuntimeException("Team not found with task id: " + teamId);
        }
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
}
