package fact.it.teamservice;

import fact.it.teamservice.dto.*;
import fact.it.teamservice.exception.DuplicateEntityException;
import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.model.Team;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.repository.TeamRepository;
import fact.it.teamservice.service.Impl.DepartmentServiceImpl;
import fact.it.teamservice.service.Impl.MemberServiceImpl;
import fact.it.teamservice.service.Impl.TeamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceApplicationTests {
    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;


    @Mock
    private WebClient webClient;

    @InjectMocks
    private MemberServiceImpl memberService;
    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void createTeam_Success() {
        // Arrange
        TeamRequest teamRequest = new TeamRequest("team-1", "Example Team", new ArrayList<>());

        when(teamRepository.existsByName(teamRequest.getName())).thenReturn(false);

        // Act
        Team result = teamService.createTeam(teamRequest);

        // Assert
        assertNotNull(result);
    }

    @Test
    void createTeam_DuplicateName_ThrowsException() {
        // Arrange
        TeamRequest teamRequest = new TeamRequest("team-1","Duplicate Team", new ArrayList<>());

        when(teamRepository.existsByName(teamRequest.getName())).thenReturn(true);

        // Act and Assert
        assertThrows(DuplicateEntityException.class, () -> teamService.createTeam(teamRequest));
    }

    private Team createTeam(String teamNumber, String name) {
        Team team = new Team();
        team.setTeamNumber(teamNumber);
        team.setName(name);
        return team;
    }
}
