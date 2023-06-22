package no.nav.dolly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@EnableAutoConfiguration
@ComponentScan("no.nav.dolly")
@AutoConfigureMockMvc(addFilters = false)
public class BestillingMalServiceTest {

    private final static String MALNAVN = "test";
    private final static String NYTT_MALNAVN = "nyttMalnavn";
    private final static String BEST_KRITERIER = "Testeteste";
    private static final Bruker DUMMY_EN = Bruker.builder()
            .brukerId("2")
            .brukernavn("test_en")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_en")
            .build();
    private static final Bruker DUMMY_TO = Bruker.builder()
            .brukerId("1")
            .brukernavn("test_to")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_to")
            .build();
    private static final String IDENT = "12345678912";
    private static final String BESKRIVELSE = "Teste";
    private static final String TESTGRUPPE = "Testgruppe";
    private static final String UGYLDIG_BESTILLINGID = "999";


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BestillingMalRepository bestillingMalRepository;
    @Autowired
    private BestillingRepository bestillingRepository;
    @Autowired
    private TestgruppeRepository testgruppeRepository;
    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void beforeEach() {
        flyway.migrate();
        bestillingRepository.deleteAll();
        bestillingMalRepository.deleteAll();
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @AfterEach
    public void afterEach() {
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    @Test
    @DisplayName("Oppretter og returnerer alle maler tilknyttet to forskjellige brukere")
    void shouldCreateAndGetMaler()
            throws Exception {

        var bruker_en = saveDummyBruker(DUMMY_EN);
        var bruker_to = saveDummyBruker(DUMMY_TO);
        saveDummyBestillingMal(bruker_en);
        saveDummyBestillingMal(bruker_to);

        mockMvc.perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.ALLE", hasSize(2)))
                .andExpect(jsonPath("$.malbestillinger.test_en", hasSize(1)))
                .andExpect(jsonPath("$.malbestillinger.test_to", hasSize(1)))
                .andExpect(jsonPath("$.malbestillinger.test_en[0].malNavn").value(MALNAVN))
                .andExpect(jsonPath("$.malbestillinger.test_en[0].bruker.brukerId").value(bruker_en.getBrukerId()))
                .andExpect(jsonPath("$.malbestillinger.test_en[0].bestilling.navSyntetiskIdent", is(true)))
                .andExpect(jsonPath("$.malbestillinger.test_to[0].bruker.brukerId").value(bruker_to.getBrukerId()));
    }

    @Test
    @DisplayName("Oppretter, endrer navn på og sletter til slutt bestillingMal")
    void shouldCreateUpdateAndDeleteMal()
            throws Exception {

        var bruker_en = saveDummyBruker(DUMMY_EN);
        var bestillingMal = saveDummyBestillingMal(bruker_en);

        mockMvc.perform(put("/api/v1/bestilling/malbestilling/{id}", bestillingMal.getId())
                        .queryParam("malNavn", NYTT_MALNAVN))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.test_en", hasSize(1)))
                .andExpect(jsonPath("$.malbestillinger.test_en[0].malNavn").value(NYTT_MALNAVN));

        mockMvc.perform(delete("/api/v1/bestilling/malbestilling/{id}", bestillingMal.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.ALLE", empty()));
    }

    @Test
    @DisplayName("Oppretter mal fra gjeldende bestilling og tester at NotFoundError blir kastet ved ugyldig bestillingId")
    void shouldCreateMalerFromExistingOrder()
            throws Exception {

        var bruker_en = saveDummyBruker(DUMMY_EN);
        var testgruppe = saveDummyGruppe();
        var bestilling = saveDummyBestilling(bruker_en, testgruppe);

        mockMvc.perform(post("/api/v1/bestilling/malbestilling")
                        .queryParam("bestillingId", String.valueOf(bestilling.getId()))
                        .queryParam("malNavn", MALNAVN))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/bestilling/malbestilling")
                        .queryParam("bestillingId", UGYLDIG_BESTILLINGID)
                        .queryParam("malNavn", MALNAVN))
                .andExpect(status().is4xxClientError());
    }

    BestillingMal saveDummyBestillingMal(Bruker bruker) {
        return bestillingMalRepository.save(
                BestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .malNavn(MALNAVN)
                        .build()
        );
    }

    Bestilling saveDummyBestilling(Bruker bruker, Testgruppe testgruppe) {
        return bestillingRepository.save(
                Bestilling
                        .builder()
                        .id(1L)
                        .gruppe(testgruppe)
                        .ferdig(false)
                        .antallIdenter(1)
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .beskrivelse(BESKRIVELSE)
                        .ident(IDENT)
                        .build()
        );
    }

    Testgruppe saveDummyGruppe() {
        return testgruppeRepository.save(
                Testgruppe.builder()
                        .opprettetAv(DUMMY_EN)
                        .sistEndretAv(DUMMY_EN)
                        .navn(TESTGRUPPE)
                        .hensikt(TESTGRUPPE)
                        .datoEndret(LocalDate.now())
                        .build()
        );
    }

    Bruker saveDummyBruker(Bruker bruker) {
        return brukerRepository.save(bruker);
    }
}