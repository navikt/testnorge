package no.nav.registre.varslingerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.varslingerservice.repository.VarslingRepository;
import no.nav.registre.varslingerservice.repository.model.VarslingModel;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class VarslingerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private VarslingRepository varslingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        varslingRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        varslingRepository.deleteAll();
    }

    @Test
    void testNoWarningsInRepository()
            throws Exception {
        mvc.perform(get("/api/v1/varslinger"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testThreeWarningsInRepository()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("2").build());
        var v3 = varslingRepository.save(VarslingModel.builder().varslingId("3").build());

        mvc.perform(get("/api/v1/varslinger"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].varslingId").value(v1.getVarslingId()))
                .andExpect(jsonPath("$[1].varslingId").value(v2.getVarslingId()))
                .andExpect(jsonPath("$[2].varslingId").value(v3.getVarslingId()));
    }

    @Test
    void testPutWarning()
            throws Exception {
        var sent = varslingRepository.save(VarslingModel.builder().varslingId("1").build());

        mvc.perform(put("/api/v1/varslinger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sent)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/varslinger/1"));

        var saved = varslingRepository
                .findById("1")
                .orElseThrow();
        assertThat(saved.getVarslingId()).isEqualTo(sent.getVarslingId());
    }

    @Test
    void deleteNonexistingWarning()
            throws Exception {
        mvc.perform(delete("/api/v1/varslinger/{id}", "some-nonexisting-id"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteExistingWarning()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("2").build());

        mvc.perform(delete("/api/v1/varslinger/{id}", v1.getVarslingId()))
                .andExpect(status().isOk());

        assertThat(varslingRepository.findById(v1.getVarslingId()))
                .isEmpty();
        assertThat(varslingRepository.findById(v2.getVarslingId()))
                .isNotEmpty();
    }

    @Test
    void getNonexistingWarning()
            throws Exception {
        mvc.perform(get("/api/v1/varslinger/{id}", "some-nonexisting-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSpecificWarning()
            throws Exception {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());

        mvc.perform(get("/api/v1/varslinger/{id}", v1.getVarslingId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.varslingId").value(v1.getVarslingId()));
    }

}
