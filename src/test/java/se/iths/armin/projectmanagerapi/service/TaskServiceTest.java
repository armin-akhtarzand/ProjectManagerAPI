package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.armin.projectmanagerapi.dto.task.TaskRequestDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskResponseDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.mapper.TaskMapperImpl;
import se.iths.armin.projectmanagerapi.repository.TaskRepository;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private TaskMapperImpl taskMapperImpl;
    @Mock
    private AppUserService appUserService;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private TaskService taskService;

    private Project project;
    private AppUser user;
    private Task task;
    private TaskResponseDto responseDto;
    private TaskRequestDto requestDto;


    @BeforeEach
    void setUp() {
        user = new AppUser();
        project = new Project();
        project.setProjectId(1L);
        project.setDescription("Test Project");
        project.setTitle("Test Project");
        user.setUserid(1L);
        user.setFirstname("FirstName");
        user.setLastname("LastName");
        LocalDate deadline = LocalDate.now();


        requestDto = new TaskRequestDto(1L, "TestTitle", "TestDescription", deadline);

        task = new Task();
        task.setAssignee(user);
        task.setProject(project);
        task.setDescription(requestDto.description());
        task.setTitle(requestDto.title());
        task.setDeadline(deadline);
    }
}
