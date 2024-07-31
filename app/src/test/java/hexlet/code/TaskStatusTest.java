package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;
    private JwtRequestPostProcessor adminToken;

    private TaskStatus testTaskStatus;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    public void setUp() {
        taskStatusRepository.deleteAll();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testAdmin = Instancio.of(modelGenerator.getUserModel()).create();
        testAdmin.setEmail("hexlet@example.com");
        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

    }

    @Test
    public void testGetAll() throws Exception {
        var request = get("/api/task_statuses").with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShowTasStatus() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = get("/api/task_statuses/" + testTaskStatus.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isPresent();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreateTaskStatusAuth() throws Exception {
        var request = post("/api/task_statuses")
                .with(adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTaskStatus));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();
        assertNotNull(taskStatus);
        assertEquals(taskStatus.getName(), testTaskStatus.getName());
        assertEquals(taskStatus.getSlug(), testTaskStatus.getSlug());
    }

    @Test
    public void testCreateTaskStatusNotAuth() throws Exception {
        var newTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newTaskStatus));
        var result = mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
        var taskStatusIsPresent = taskStatusRepository.findBySlug(newTaskStatus.getSlug()).isPresent();
        assertFalse(taskStatusIsPresent);
    }

    @Test
    public void testUpdateTaskStatusAuth() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var newData = new HashMap<String, String>();
        newData.put("slug", "newSlug");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newData));

        var result = mockMvc.perform(request).andExpect(status().isOk());
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertNotNull(taskStatus);
        assertEquals(newData.get("slug"), taskStatus.getSlug());
        assertEquals(testTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void testUpdateTaskStatusNotAuth() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var newData = new HashMap<String, String>();
        newData.put("slug", "newSlug");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newData));

        var result = mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
        assertTrue(taskStatusRepository.findBySlug(newData.get("slug")).isEmpty());
    }

    @Test
    public void testDestroyTaskStatusAuth() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var request = delete("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        var result = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();
        assertTrue(taskStatusRepository.findBySlug(testTaskStatus.getSlug()).isEmpty());
    }

    @Test
    public void testDestroyTaskStatusNotAuth() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var request = delete("/api/task_statuses/" + testTaskStatus.getId());
        var result = mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
        assertTrue(taskStatusRepository.findBySlug(testTaskStatus.getSlug()).isPresent());
    }
}
