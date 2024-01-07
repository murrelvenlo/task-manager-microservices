package fact.it.taskservice;

import fact.it.taskservice.dto.TaskRequest;
import fact.it.taskservice.dto.TaskResponse;
import fact.it.taskservice.exception.DuplicateEntityException;
import fact.it.taskservice.model.Task;
import fact.it.taskservice.model.TaskStatus;
import fact.it.taskservice.repository.TaskRepository;
import fact.it.taskservice.service.Impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceApplicationTests {

    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper mapper;

    @Test
    public void testCreateTaskDuplicateCheck() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Sample Task");
        taskRequest.setStatus(TaskStatus.valueOf("PENDING"));
        taskRequest.setDescription("Sample description");

        // Act
        taskService.createTask(taskRequest);

        // Assert
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testCreateTask() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Sample Task");
        taskRequest.setStatus(TaskStatus.valueOf("PENDING"));
        taskRequest.setDescription("Sample description");

        // Mock repository behavior
        when(taskRepository.existsByNameAndStatus(taskRequest.getName(), taskRequest.getStatus()))
                .thenReturn(false);

        // Act
        taskService.createTask(taskRequest);

        // Assert
        verify(taskRepository).save(argThat(task -> {
            // Add your specific assertions for the task object here
            assertNotNull(task.getTaskCode());
            assertTrue(task.getTaskCode().startsWith("task-"));
            assertEquals(taskRequest.getName(), task.getName());
            assertEquals(taskRequest.getStatus(), task.getStatus());

            return true;
        }));
    }

    @Test
    public void testCreateTaskWithDuplicate() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Sample Task");
        taskRequest.setStatus(TaskStatus.valueOf("PENDING"));
        taskRequest.setDescription("Sample description");

        when(taskRepository.existsByNameAndStatus(taskRequest.getName(), taskRequest.getStatus()))
                .thenReturn(true);

        // Act & Assert
        DuplicateEntityException exception = assertThrows(DuplicateEntityException.class,
                () -> taskService.createTask(taskRequest));
    }


    @Test
    public void testGetAllTasks() {
        // Arrange
        Task task1 = Task.builder()
                .taskCode("task-1234567")
                .name("Task 1")
                .status(TaskStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 1")
                .build();

        Task task2 = Task.builder()
                .taskCode("task-7654321")
                .name("Task 2")
                .status(TaskStatus.DONE)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 2")
                .build();

        List<Task> taskList = Arrays.asList(task1, task2);

        when(taskRepository.findAll()).thenReturn(taskList);

        TaskResponse taskResponse1 = TaskResponse.builder()
                .taskCode("task-1234567")
                .name("Task 1")
                .status(TaskStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 1")
                .build();

        TaskResponse taskResponse2 = TaskResponse.builder()
                .taskCode("task-7654321")
                .name("Task 2")
                .status(TaskStatus.DONE)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 2")
                .build();

        List<TaskResponse> expectedResponses = Arrays.asList(taskResponse1, taskResponse2);

        when(mapper.map(task1, TaskResponse.class)).thenReturn(taskResponse1);
        when(mapper.map(task2, TaskResponse.class)).thenReturn(taskResponse2);

        // Act
        List<TaskResponse> actualResponses = taskService.getAllTasks();

        // Assert
        assertEquals(expectedResponses, actualResponses);

        // Verify that findAll() and mapper.map(...) were called
        verify(taskRepository, times(1)).findAll();
        verify(mapper, times(2)).map(any(), eq(TaskResponse.class));
    }

//    @Test
//    public void testFindTaskByTaskCode_ExistingTask() {
//        // Arrange
//        String taskCode = "task-6130914";
//        Task task = Task.builder()
//                .taskCode(taskCode)
//                .name("Sample For Test")
//                .status(TaskStatus.NOT_DONE)
//                .creationDate(LocalDateTime.now())
//                .dueDate(new Date())
//                .description("Test desc for test.")
//                .build();
//
//        when(taskRepository.findTaskByTaskCode(taskCode)).thenReturn(task);
//
//        TaskResponse expectedResponse = TaskResponse.builder()
//                .taskCode(taskCode)
//                .name("Sample Task")
//                .status(TaskStatus.NOT_DONE)
//                .creationDate(LocalDateTime.now())
//                .dueDate(new Date())
//                .description("Test desc for test.")
//                .build();
//
//        // Act
//        TaskResponse actualResponse = taskService.findTaskByTaskCode(taskCode);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse, actualResponse);
//
//        // Verify that findTaskByTaskCode was called with the correct taskCode
//        verify(taskRepository, times(1)).findTaskByTaskCode(taskCode);
//    }

    @Test
    public void testFindTaskByTaskCode_NonExistingTask() {
        // Arrange
        String taskCode = "non-existing-task";
        when(taskRepository.findTaskByTaskCode(taskCode)).thenReturn(null);

        // Act
        TaskResponse actualResponse = taskService.findTaskByTaskCode(taskCode);

        // Assert
        assertNull(actualResponse);

        // Verify that findTaskByTaskCode was called with the correct taskCode
        verify(taskRepository, times(1)).findTaskByTaskCode(taskCode);
    }

    @Test
    void testGetAllTasksByrNumber() {
        // Arrange
        String rNumber = "R123";

        Task task1 = Task.builder()
                .taskCode("task-1234567")
                .name("Task 1")
                .status(TaskStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 1")
                .rNumber(rNumber)
                .build();

        Task task2 = Task.builder()
                .taskCode("task-7654321")
                .name("Task 2")
                .status(TaskStatus.DONE)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 2")
                .rNumber(rNumber)
                .build();

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findAllByrNumber(rNumber)).thenReturn(tasks);

        TaskResponse taskResponse1 = TaskResponse.builder()
                .taskCode("task-1234567")
                .name("Task 1")
                .status(TaskStatus.PENDING)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 1")
                .build();

        TaskResponse taskResponse2 = TaskResponse.builder()
                .taskCode("task-7654321")
                .name("Task 2")
                .status(TaskStatus.DONE)
                .creationDate(LocalDateTime.now())
                .dueDate(new Date())
                .description("Description 2")
                .build();

        List<TaskResponse> expectedResponses = Arrays.asList(taskResponse1, taskResponse2);

        when(mapper.map(task1, TaskResponse.class)).thenReturn(taskResponse1);
        when(mapper.map(task2, TaskResponse.class)).thenReturn(taskResponse2);

        // Act
        List<TaskResponse> actualResponses = taskService.getAllTasksByrNumber(rNumber);

        // Assert
        assertEquals(expectedResponses, actualResponses);

        // Verify that findAllByrNumber() and mapper.map(...) were called
        verify(taskRepository, times(1)).findAllByrNumber(rNumber);
        verify(mapper, times(2)).map(any(), eq(TaskResponse.class));
    }


}
