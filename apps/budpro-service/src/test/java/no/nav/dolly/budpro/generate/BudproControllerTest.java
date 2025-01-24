package no.nav.dolly.budpro.generate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.budpro.navn.GeneratedNameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Slf4j
class BudproControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeneratedNameService generatedNameService;

    private AutoCloseable closeable;

    @BeforeEach
    void before() {
        closeable = MockitoAnnotations.openMocks(this);

        var names = new ArrayList<String>(100);
        for (int i = 0; i < 100; i++) {
            names.add("Personnavn " + i);
        }

        when(generatedNameService.getNames(any(), anyInt()))
                .thenReturn(names.toArray(new String[0]));
    }

    @AfterEach
    void after() throws Exception {
        closeable.close();
    }

    @Test
    void thatNoSeedGivesDifferentResults()
            throws Exception {
        var result1 = mockMvc
                .perform(get("/api/random?limit={limit}", 50))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var result2 = mockMvc
                .perform(get("/api/random?limit={limit}", 50))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result1)
                .isNotEqualTo(result2);
    }

    @Test
    void thatSameSeedGivesSameResults()
            throws Exception {
        var result1 = mockMvc
                .perform(get("/api/random?seed={seed}&limit={limit}", 123L, 50))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var result2 = mockMvc
                .perform(get("/api/random?seed={seed}&limit={limit}", 123L, 50))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(result1)
                .isEqualTo(result2);
    }

    @Test
    void thatOverrideWorksAsIntended()
            throws Exception {
        var override = new BudproRecord(
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE"
        );
        var json = mockMvc
                .perform(post("/api/random?limit={limit}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(override)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var result = objectMapper.readValue(json, BudproRecord[].class);
        assertThat(result).allMatch(element -> element.equals(override));
    }

}
