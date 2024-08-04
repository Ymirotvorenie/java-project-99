package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.domain.label.mapper.LabelMapper;
import hexlet.code.domain.label.model.Label;
import hexlet.code.domain.label.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("test@mail.com"));
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels").with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexNotAuth() throws Exception {
        var request = get("/api/labels");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        labelRepository.save(testLabel);
        var request = get("/api/labels/{id}", testLabel.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isPresent().and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testShowNotAuth() throws Exception {
        labelRepository.save(testLabel);
        var request = get("/api/labels/{id}", testLabel.getId());
        var result = mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testLabel));
        mockMvc.perform(request).andExpect(status().isCreated());
        var label = labelRepository.findByName(testLabel.getName());

        assertTrue(label.isPresent());
        assertEquals(label.get().getName(), testLabel.getName());
    }

    @Test
    public void testCreateNotAuth() throws Exception {
        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testLabel));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        labelRepository.save(testLabel);
        var dto = mapper.map(testLabel);
        dto.setName("New name");
        var request = put("/api/labels/{id}", testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isOk());

        var isLabelUpdate = labelRepository.findByName(dto.getName()).isPresent();
        assertTrue(isLabelUpdate);
    }

    @Test
    public void testUpdateNotAuth() throws Exception {
        labelRepository.save(testLabel);
        var dto = mapper.map(testLabel);
        dto.setName("New name");
        var request = put("/api/labels/{id}", testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testDestroy() throws Exception {
        labelRepository.save(testLabel);

        var request = delete("/api/labels/{id}", testLabel.getId())
                .with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
        var isLabelNotExist = labelRepository.findByName(testLabel.getName()).isEmpty();
        assertTrue(isLabelNotExist);
    }

    @Test
    public void testDestroyNotAuth() throws Exception {
        labelRepository.save(testLabel);

        var request = delete("/api/labels/{id}", testLabel.getId());
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }
}
