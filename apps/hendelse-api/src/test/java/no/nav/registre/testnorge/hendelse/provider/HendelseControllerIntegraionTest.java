package no.nav.registre.testnorge.hendelse.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.hendelse.repository.HendelseRepository;
import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class HendelseControllerIntegraionTest {

    private static final String FIRST_IDENT = "01010123964";
    private static final String SECOND_IDENT = "02012134961";

    private final LocalDate now = LocalDate.now();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HendelseRepository repository;

    @Test
    void should_get_hendelse() throws Exception {
        repository.save(createModel(
                HendelseType.SYKEMELDING_OPPRETTET,
                FIRST_IDENT,
                now,
                now.plusDays(2)
        ));

        String json = mvc.perform(get("/api/v1/hendelser"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HendelseDTO[] hendelseer = objectMapper.readValue(json, HendelseDTO[].class);
        assertThat(hendelseer)
                .hasSize(1)
                .contains(createHendelse(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ));
    }

    @Test
    void should_get_hendelse_for_ident() throws Exception {
        LocalDate now = LocalDate.now();
        repository.saveAll(Arrays.asList(
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                )
        ));

        String json = mvc.perform(get("/api/v1/hendelser/").param("ident", FIRST_IDENT))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HendelseDTO[] hendelseer = objectMapper.readValue(json, HendelseDTO[].class);
        assertThat(hendelseer)
                .hasSize(1)
                .containsOnly(createHendelse(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ));
    }

    @Test
    void should_get_hendelse_between_date() throws Exception {
        LocalDate now = LocalDate.now();
        repository.saveAll(Arrays.asList(
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                )
        ));

        String json = mvc.perform(
                get("/api/v1/hendelser/")
                        .param("between", format(now.minusYears(1))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HendelseDTO[] hendelseer = objectMapper.readValue(json, HendelseDTO[].class);
        assertThat(hendelseer)
                .hasSize(1)
                .containsOnly(createHendelse(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                ));
    }


    @Test
    void should_get_SYKEMELDING_OPPRETTET_hendelse() throws Exception {
        LocalDate now = LocalDate.now();
        repository.saveAll(Arrays.asList(
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.ARBEIDSFORHOLD_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                )
        ));

        String json = mvc.perform(
                get("/api/v1/hendelser/").param("type", HendelseType.SYKEMELDING_OPPRETTET.name())
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HendelseDTO[] hendelseer = objectMapper.readValue(json, HendelseDTO[].class);
        assertThat(hendelseer)
                .hasSize(1)
                .containsOnly(createHendelse(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ));
    }


    @Test
    void should_get_ARBEIDSFORHOLD_OPPRETTET_hendelse_for_indent_and_between_date() throws Exception {
        LocalDate now = LocalDate.now();
        repository.saveAll(Arrays.asList(
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        FIRST_IDENT,
                        now,
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.ARBEIDSFORHOLD_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.SYKEMELDING_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                ),
                createModel(
                        HendelseType.ARBEIDSFORHOLD_OPPRETTET,
                        FIRST_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                )
        ));

        String json = mvc.perform(
                get("/api/v1/hendelser/")
                        .param("type", HendelseType.ARBEIDSFORHOLD_OPPRETTET.name())
                        .param("between", format(now.minusYears(1)))
                        .param("ident", SECOND_IDENT)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HendelseDTO[] hendelseer = objectMapper.readValue(json, HendelseDTO[].class);
        assertThat(hendelseer)
                .hasSize(1)
                .containsOnly(createHendelse(
                        HendelseType.ARBEIDSFORHOLD_OPPRETTET,
                        SECOND_IDENT,
                        now.minusYears(1),
                        now.plusDays(2)
                ));
    }


    private HendelseModel createModel(HendelseType type, String ident, LocalDate start, LocalDate end) {
        return HendelseModel
                .builder()
                .hendelse(type)
                .ident(ident)
                .fom(Date.valueOf(start))
                .tom(end != null ? Date.valueOf(end) : null)
                .build();
    }

    private HendelseDTO createHendelse(HendelseType type, String ident, LocalDate start, LocalDate end) {
        return HendelseDTO
                .builder()
                .ident(ident)
                .fom(start)
                .tom(end)
                .type(type)
                .build();
    }


    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

}