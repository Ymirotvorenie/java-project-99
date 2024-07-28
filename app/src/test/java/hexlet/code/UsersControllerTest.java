package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

    }
    @Test
    public void testIndex() throws Exception {
        var request = get("/users").with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testCreateUser() throws Exception {
        var request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(testUser.getEmail()).get();
        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());

    }

    @Test
    public void testShowUser() throws Exception {
        userRepository.save(testUser);
        var request = get("/users/" + testUser.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isPresent();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName())
        );
    }

    @Test
    public void testUpdateUser() throws Exception {
        userRepository.save(testUser);

        var data = new HashMap<>();
        data.put("email", "test@test.ru");

        var request = put("/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request).andExpect(status().isOk());

        assertEquals(userRepository.findById(testUser.getId()).get().getEmail(), "test@test.ru");
        assertEquals(userRepository.findById(testUser.getId()).get().getFirstName(), testUser.getFirstName());
    }

    @Test
    public void testDestroyUser() throws Exception {

        userRepository.save(testUser);
        var request = delete("/users/" + testUser.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(userRepository.findAll().size(), 0);
        assertTrue(userRepository.findById(testUser.getId()).isEmpty());
    }
}

