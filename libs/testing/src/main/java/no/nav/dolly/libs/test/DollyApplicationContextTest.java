package no.nav.dolly.libs.test;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Common test base for all tests that simply want to check the application context.
 * Also does a simple check to see if the application is alive and ready.
 * Note that this class is intentionally not annotated with {@link DollySpringBootTest}.
 */
@AutoConfigureMockMvc(addFilters = false)
public class DollyApplicationContextTest {

    @Setter(onMethod_ = @Autowired)
    private MockMvc mockMvc;

    @Test
    void isAlive() throws Exception {
        mockMvc
                .perform(get("/internal/isAlive"))
                .andExpect(status().isOk());
    }

    @Test
    void isReady() throws Exception {
        mockMvc
                .perform(get("/internal/isReady"))
                .andExpect(status().isOk());
    }

}
