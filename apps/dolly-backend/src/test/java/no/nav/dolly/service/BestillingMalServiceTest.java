package no.nav.dolly.service;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@EnableAutoConfiguration
@ComponentScan("no.nav.dolly")
@AutoConfigureMockMvc(addFilters = false)
class BestillingMalServiceTest {

    private final static String MALNAVN = "test";
    private final static String NYTT_MALNAVN = "nyttMalnavn";
    private final static String BEST_KRITERIER = "Testeteste";
    private static final Bruker DUMMY_EN = Bruker.builder()
            .brukerId("testbruker_en")
            .brukernavn("test_en")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_en")
            .build();
    private static final Bruker DUMMY_TO = Bruker.builder()
            .brukerId("testbruker_to")
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
    private BrukerFavoritterRepository brukerFavoritterRepository;
    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private IdentRepository identRepository;
    @Autowired
    private Flyway flyway;

    @Transactional
    @BeforeEach
    public void beforeEach() {
        flyway.migrate();
        saveDummyBruker(DUMMY_EN);
        saveDummyBruker(DUMMY_TO);
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Transactional
    @AfterEach
    public void afterEach() {
        deleteAllDatabaseContent();
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    @Test
    @Transactional
    @DisplayName("Oppretter og returnerer alle maler tilknyttet to forskjellige brukere")
    void shouldCreateAndGetMaler()
            throws Exception {

        var bruker_en = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bruker_to = brukerRepository.findBrukerByBrukerId(DUMMY_TO.getBrukerId()).orElseThrow();
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
    @Transactional
    @DisplayName("Oppretter mal fra gjeldende bestilling og tester at NotFoundError blir kastet ved ugyldig bestillingId")
    void shouldCreateMalerFromExistingOrder()
            throws Exception {

        var bruker_en = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
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

    @Test
    @Transactional
    @DisplayName("Oppretter, endrer navn på og sletter til slutt bestillingMal")
    void shouldCreateUpdateAndDeleteMal()
            throws Exception {

        var bruker_en = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bestillingMal = saveDummyBestillingMal(bruker_en);

        mockMvc.perform(put("/api/v1/bestilling/malbestilling/{id}", bestillingMal.getId())
                        .queryParam("malNavn", NYTT_MALNAVN))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.test_en", hasSize(1)));

        mockMvc.perform(delete("/api/v1/bestilling/malbestilling/{id}", bestillingMal.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.ALLE", empty()));
    }


    BestillingMal saveDummyBestillingMal(Bruker bruker) {
        return bestillingMalRepository.save(
                BestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .malNavn(MALNAVN)
                        .sistOppdatert(LocalDateTime.now())
                        .build()
        );
    }

    Bestilling saveDummyBestilling(Bruker bruker, Testgruppe testgruppe) {
        return bestillingRepository.save(
                Bestilling
                        .builder()
                        .gruppe(testgruppe)
                        .ferdig(false)
                        .antallIdenter(1)
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .beskrivelse(BESKRIVELSE)
                        .sistOppdatert(LocalDateTime.now())
                        .ident(IDENT)
                        .navSyntetiskIdent(true)
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

    void saveDummyBruker(Bruker bruker) {
        brukerRepository.save(bruker);
    }

    void deleteAllDatabaseContent() {
        bestillingMalRepository.deleteAll();
        bestillingRepository.deleteAll();
        identRepository.deleteAll();
        brukerFavoritterRepository.deleteAll();
        testgruppeRepository.findAll(Sort.unsorted()).forEach(gruppe -> testgruppeRepository.deleteTestgruppeById(gruppe.getId()));
        brukerRepository.deleteByBrukerId(DUMMY_EN.getBrukerId());
        brukerRepository.deleteByBrukerId(DUMMY_TO.getBrukerId());
    }
}