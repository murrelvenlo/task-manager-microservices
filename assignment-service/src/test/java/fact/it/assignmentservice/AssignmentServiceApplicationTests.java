package fact.it.assignmentservice;

import static fact.it.assignmentservice.model.TaskAssignmentStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fact.it.assignmentservice.dto.*;
import fact.it.assignmentservice.model.TaskAssignment;
import fact.it.assignmentservice.model.TaskAssignmentStatus;
import fact.it.assignmentservice.repository.TaskAssignmentRepository;
import fact.it.assignmentservice.service.Impl.TaskAssignmentServiceImpl;
import fact.it.assignmentservice.service.TaskAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceApplicationTests {
	@Mock
	private WebClient webClient;

	@Mock
	private TaskAssignmentRepository assignmentRepository;

	@InjectMocks
	private TaskAssignmentServiceImpl taskAssignmentService;

	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	private WebClient.RequestHeadersSpec requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;
	@Mock
	private ModelMapper mapper;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(taskAssignmentService, "taskServiceBaseUrl", "http://localhost:8080");
		ReflectionTestUtils.setField(taskAssignmentService, "teamServiceBaseUrl", "http://localhost:8081");
		ReflectionTestUtils.setField(taskAssignmentService, "emailServiceBaseUrl", "http://localhost:8082");
	}
//	@Test
//	public void testCreateAssignment_Success() {
//		// Arrange
//
//		String assignmentCode = "asnmt-123569";
//		String taskCode = "task-012356";
//		String rNumber = "r0123456";
//		boolean completed = false;
//		LocalDateTime deadline = LocalDateTime.now();
//		String notes = "Test notes";
//
//		AssignmentRequest assignmentRequest = new AssignmentRequest();
//
//		MemberResponse memberResponse = new MemberResponse();
//		// populate memberResponse with test data
//		memberResponse.setRNumber(rNumber);
//
//		TaskResponse taskResponse = new TaskResponse();
//		// populate taskResponse with test data
//		taskResponse.setTaskCode(taskCode);
//
//		TaskAssignment assignment = new TaskAssignment();
//		assignment.setId("1");
//		assignment.setStatus(TaskAssignmentStatus.NOT_COMPLETED);
//		assignment.setCompleted(completed);
//		assignment.setAssignmentCode(assignmentCode);
//		assignment.setDeadline(deadline);
//		assignment.setAssignmentDate(LocalDateTime.now());
//		assignment.setNotes(notes);
//
//		when(assignmentRepository.save(any(TaskAssignment.class))).thenReturn(assignment);
//
//		when(webClient.get()).thenReturn(requestHeadersUriSpec);
//		when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
//		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(MemberResponse.class)).thenReturn(Mono.just(new MemberResponse()));
//		when(responseSpec.bodyToMono(TaskResponse.class)).thenReturn(Mono.just(new TaskResponse()));
//
//		// Act
//		taskAssignmentService.createAssignment(assignmentRequest);
//
//		// Assert
//		// verify
//		verify(assignmentRepository, times(1)).save(any(TaskAssignment.class));
//
//	}

	@Test
	public void testGetAssignmentsByRNumberOrTaskCode() {
		// Arrange
		List<TaskAssignment> assignments = Arrays.asList(
				new TaskAssignment("1", "asnmt-123569", "task-012356", "r0123456", false, LocalDateTime.now(), LocalDateTime.now(), "Test notes", TaskAssignmentStatus.NOT_COMPLETED)

		);
		when(assignmentRepository.findByrNumberOrTaskCodeOrAssignmentCode(anyString(), anyString(), anyString())).thenReturn(assignments);
		when(mapper.map(any(TaskAssignment.class), eq(AssignmentResponse.class))).thenAnswer(invocation -> {
			TaskAssignment assignment = invocation.getArgument(0);
			AssignmentResponse response = new AssignmentResponse();

			return response;
		});

		// Act
		List<AssignmentResponse> result = taskAssignmentService.getAssignmentsByRNumberOrTaskCode("r0123456", "task-012356", "asnmt-123569");

		// Assert
		assertEquals(assignments.size(), result.size());
	}

	@Test
	public void testGetAllAssignments() {
		// Arrange
		List<TaskAssignment> assignments = Arrays.asList(
				new TaskAssignment("1", "asnmt-123569", "task-012356", "r0123456", false, LocalDateTime.now(), LocalDateTime.now(), "Test notes", TaskAssignmentStatus.NOT_COMPLETED)
		);
		when(assignmentRepository.findAll()).thenReturn(assignments);
		when(mapper.map(any(TaskAssignment.class), eq(AssignmentResponse.class))).thenAnswer(invocation -> {
			TaskAssignment assignment = invocation.getArgument(0);
			AssignmentResponse response = new AssignmentResponse();
			return response;
		});

		// Act
		List<AssignmentResponse> result = taskAssignmentService.getAllAssignments();

		// Assert
		assertEquals(assignments.size(), result.size());
	}

	@Test
	public void testUpdateAssignment() {
		// Arrange
		String assignmentCode = "asnmt-123569";
		AssignmentRequest assignmentRequest = new AssignmentRequest();

		TaskAssignment existingAssignment = new TaskAssignment("1", assignmentCode, "task-012356", "r0123456", false, LocalDateTime.now(), LocalDateTime.now(), "Test notes", TaskAssignmentStatus.NOT_COMPLETED);
		when(assignmentRepository.findByAssignmentCode(assignmentCode)).thenReturn(existingAssignment);

		// Act
		taskAssignmentService.updateAssignment(assignmentCode, assignmentRequest);

		// Assert
		verify(assignmentRepository, times(1)).save(any(TaskAssignment.class));
	}

	@Test
	public void testDeleteAssignment() {
		// Arrange
		String assignmentCode = "asnmt-123569";
		TaskAssignment existingAssignment = new TaskAssignment("1", assignmentCode, "task-012356", "r0123456", false, LocalDateTime.now(), LocalDateTime.now(), "Test notes", TaskAssignmentStatus.NOT_COMPLETED);
		when(assignmentRepository.findByAssignmentCode(assignmentCode)).thenReturn(existingAssignment);

		// Act
		taskAssignmentService.deleteAssignment(assignmentCode);

		// Assert
		verify(assignmentRepository, times(1)).deleteByAssignmentCode(assignmentCode);
	}

}
