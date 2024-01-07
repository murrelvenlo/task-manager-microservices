package fact.it.assignmentservice;

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

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(taskAssignmentService, "taskServiceBaseUrl", "http://localhost:8080");
		ReflectionTestUtils.setField(taskAssignmentService, "teamServiceBaseUrl", "http://localhost:8081");
		ReflectionTestUtils.setField(taskAssignmentService, "emailServiceBaseUrl", "http://localhost:8082");
	}

	@Test
	public void testCreateAssignment() {
		// Mock the WebClient response for getTaskByCode and getMemberByRNumber
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(anyString(),  any(Function.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(TaskResponse.class))
				.thenReturn(Mono.just(TaskResponse.builder().taskCode("task-6130914").build()));
		when(responseSpec.bodyToMono(MemberResponse.class))
				.thenReturn(Mono.just(MemberResponse.builder().email("test@example.com").firstName("John").taskCode("task-6130914").build()));

		// Mock the generateAssignmentCode method
		when(taskAssignmentService.generateAssignmentCode()).thenReturn("asnmt-1234567");


		// Create an AssignmentRequest
		AssignmentRequest assignmentRequest = AssignmentRequest.builder()
				.taskCode("task-6130914")
				.rNumber("123")
				.deadline(LocalDateTime.now())
				.status(TaskAssignmentStatus.NOT_COMPLETED)
				.completed(false)
				.notes("Test Assignment")
				.build();

		// Call the createAssignment method
		taskAssignmentService.createAssignment(assignmentRequest);

		// Verify that save method was called with the correct TaskAssignment object
		verify(assignmentRepository).save(any(TaskAssignment.class));

		// You can add more assertions based on your specific requirements
	}


	@Test
	public void testGetAssignmentsByRNumberOrTaskCodeOrAssignmentCode() {
		// Arrange
		String rNumber = "123";
		String taskCode = "task-6130914";
		String assignmentCode = "asnmt-1234567";

		List<TaskAssignment> mockAssignments = Collections.singletonList(
				TaskAssignment.builder()
						.assignmentCode("asnmt-1234567")
						.taskCode("task-6130914")
						.rNumber("123")
						.deadline(LocalDateTime.now())
						.status(TaskAssignmentStatus.NOT_COMPLETED)
						.completed(false)
						.notes("Test Assignment")
						.build()
		);

		when(assignmentRepository.findByrNumberOrTaskCodeOrAssignmentCode(rNumber, taskCode, assignmentCode))
				.thenReturn(mockAssignments);

		// Act
		List<AssignmentResponse> assignmentResponses = taskAssignmentService.getAssignmentsByRNumberOrTaskCode(rNumber, taskCode, assignmentCode);

		// Assert
		assertEquals(1, assignmentResponses.size());
		assertEquals("asnmt-1234567", assignmentResponses.get(0).getAssignmentCode());
		assertEquals("task-6130914", assignmentResponses.get(0).getTaskCode());
		assertEquals("123", assignmentResponses.get(0).getRNumber());

		// verify
		verify(assignmentRepository).findByrNumberOrTaskCodeOrAssignmentCode(rNumber, taskCode, assignmentCode);
	}





}
