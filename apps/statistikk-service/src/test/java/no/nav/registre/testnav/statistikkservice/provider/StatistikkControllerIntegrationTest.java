package no.nav.registre.testnav.statistikkservice.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import no.nav.registre.testnav.statistikkservice.repository.StatistikkRepository;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkValueType;
import no.nav.registre.testnav.statistikkservice.repository.model.StatistikkModel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class StatistikkControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StatistikkRepository repository;

    @Test
    @SneakyThrows
    void should_get_statstikk_from_database() {
        repository.save(StatistikkModel.builder()
                .valueType(StatistikkValueType.PERCENTAGE)
                .value(0.05)
                .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                .build());

        String json = mvc.perform(get("/api/v1/statistikk"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        StatistikkDTO[] actual = objectMapper.readValue(json, StatistikkDTO[].class);

        assertThat(actual).hasSize(1).contains(StatistikkDTO
                .builder()
                .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                .value(0.05)
                .valueType(StatistikkValueType.PERCENTAGE)
                .build()
        );
    }

    @Test
    @SneakyThrows
    void should_save_statistikk_in_database() {

        StatistikkDTO dto = StatistikkDTO
                .builder()
                .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                .value(0.05)
                .valueType(StatistikkValueType.PERCENTAGE)
                .build();

        mvc.perform(put("/api/v1/statistikk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
        ).andExpect(status().isCreated());

        Optional<StatistikkModel> model = repository.findById(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT);
        assertThat(model).isPresent().get().isEqualToIgnoringGivenFields(
                StatistikkModel.builder()
                        .valueType(StatistikkValueType.PERCENTAGE)
                        .value(0.05)
                        .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                        .build(),
                "createdAt", "updatedAt"
        );
    }

    @Test
    @SneakyThrows
    void should_update_statistikk_in_database() {

        repository.save(StatistikkModel.builder()
                .valueType(StatistikkValueType.PERCENTAGE)
                .value(0.05)
                .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                .build());

        StatistikkDTO dto = StatistikkDTO
                .builder()
                .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                .value(0.10)
                .valueType(StatistikkValueType.PERCENTAGE)
                .build();

        mvc.perform(put("/api/v1/statistikk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
        ).andExpect(status().isCreated());

        assertThat(repository.findAll())
                .hasSize(1)
                .usingElementComparatorIgnoringFields("createdAt", "updatedAt")
                .contains(StatistikkModel
                        .builder()
                        .valueType(StatistikkValueType.PERCENTAGE)
                        .value(0.10)
                        .type(StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT)
                        .build()
                );
    }


    @AfterEach
    public void cleanup() {
        repository.flush();
    }

}