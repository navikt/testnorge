package no.nav.dolly.provider.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DELETE /api/v1/gruppe")
class TestgruppeControllerDeleteTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Sletter Testgruppe")
    void deleteTestgruppe()
            throws Exception {

        var testgruppe = super.createTestgruppe("Testgruppe", super.createBruker());
        mockMvc
                .perform(get("/api/v1/gruppe/{id}", testgruppe.getId()))
                .andExpect(status().isOk());
        mockMvc
                .perform(delete("/api/v1/gruppe/{id}", testgruppe.getId()))
                .andExpect(status().isOk());
        mockMvc
                .perform(get("/api/v1/gruppe/{id}", testgruppe.getId()))
                .andExpect(status().isNotFound());
        assertThat(super.findTestgruppeById(testgruppe.getId())).isEmpty();
    }
}
