package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.domain.task.mapper.TaskMapper;
import hexlet.code.domain.label.model.Label;
import hexlet.code.domain.task.model.Task;
import hexlet.code.domain.label.repository.LabelRepository;
import hexlet.code.domain.task.repository.TaskRepository;
import hexlet.code.domain.taskStatus.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.UserUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private final UserUtils userUtils = new UserUtils();

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private JwtRequestPostProcessor token;
    private Task testTask;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();

        token = jwt().jwt(builder -> builder.subject("test@mail.com"));
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();

        testTask.setAssignee(userUtils.getTestUser());
        testTask.setTaskStatus(taskStatusRepository.findBySlug("draft").orElseThrow());

        var labels = new ArrayList<Label>();
        var label = labelRepository.findById(1L).orElseThrow();
        labels.add(label);
        testTask.setLabels(labels);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks").with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isPresent();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = mapper.map(testTask);

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isCreated());

        var task = taskRepository.findByName(dto.getTitle()).orElseThrow();

        assertThat(task.getName()).isEqualTo(dto.getTitle());
        assertThat(task.getDescription()).isEqualTo(dto.getContent());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(dto.getStatus());
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);

        var dto = mapper.map(testTask);
        dto.setTitle("new title");
        dto.setContent("new description");

        var request = put("/api/tasks/{id}", testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();

        assertThat(task.getName()).isEqualTo(dto.getTitle());
        assertThat(task.getDescription()).isEqualTo(dto.getContent());
    }

    @Test
    public void testDestroy() throws Exception {
        taskRepository.save(testTask);

        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();
        var isTaskNotExist = taskRepository.findById(testTask.getId()).isEmpty();
        assertTrue(isTaskNotExist);
    }

    @Test
    public void testSpecificationIndex() throws Exception {
        taskRepository.save(testTask);

        var request = get("/api/tasks?titleCont=create&assigneeId=1&status=to_be_fixed&labelId=1")
                .with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(task ->
                    assertThatJson(task)
                            .and(v -> v.node("name").asString().containsIgnoringCase(testTask.getName()))
                            .and(v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()))
                            .and(v -> v.node("labelId").isEqualTo(1))
                            .and(v -> v.node("taskStatus").asString().isEqualTo("draft"))
                );
    }
}
