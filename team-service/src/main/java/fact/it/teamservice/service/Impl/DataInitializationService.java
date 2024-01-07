package fact.it.teamservice.service.Impl;

import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.model.Team;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.repository.TeamRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class DataInitializationService {

    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void initializeData() {
        loadDepartments();
        loadTeams();
        loadMembers();
    }

    private void loadDepartments() {
        if (departmentRepository.count() <= 0) {
            List<Department> departments = List.of(
                    Department.builder().name("Application Development").depCode("APP-3").build(),
                    Department.builder().name("Cloud & Cyber Security").depCode("CCS-3").build(),
                    Department.builder().name("Artificial intelligence").depCode("AI-3").build()
            );

            departmentRepository.saveAll(departments);
            log.info("Departments loaded successfully");
        }
    }

    private void loadTeams() {
        if (teamRepository.count() <= 0) {
            List<Team> teams = List.of(
                    Team.builder().name("Team 1").teamNumber("team-0123456789").members(new ArrayList<>()).build(),
                    Team.builder().name("Team 2").teamNumber("team-1234567860").members(new ArrayList<>()).build(),
                    Team.builder().name("Team 3").teamNumber("team-5123406789").members(new ArrayList<>()).build()
            );

            teamRepository.saveAll(teams);
            log.info("Teams loaded successfully");
        }
    }

    private void loadMembers() {
        if (memberRepository.count() <= 0) {
            Department department = departmentRepository.findByName("Application Development");
            List<Team> teams = teamRepository.findAll(); // Retrieve existing teams

            List<Member> members = List.of(
                    Member.builder().rNumber("r0781309").firstName("Murrel").lastName("Venlo").email("venlo.mj@hotmail.nl").department(department).taskCode("task-123456").team(teams.get(0)).build(),
                    Member.builder().rNumber("r0123456").firstName("Jurmen").lastName("Prijor").email("r0781309@student.thomasmore.be").department(department).taskCode("task-0246139").team(teams.get(1)).build()
            );

            memberRepository.saveAll(members);
            log.info("Members loaded successfully");
        }
    }

}
