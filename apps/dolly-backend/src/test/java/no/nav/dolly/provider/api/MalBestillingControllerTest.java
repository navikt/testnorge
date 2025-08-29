package no.nav.dolly.provider.api;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MalBestillingControllerTest {

    private static final String MALNAVN_EN = "testmalEn";
    private static final String MALNAVN_TO = "testmalTo";
    private static final String NYTT_MALNAVN = "nyttMalnavn";
    private static final String BEST_KRITERIER = "{\"test\":true}";
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

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;
    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;
    @MockitoBean
    @SuppressWarnings("unused")
    private BrukerService brukerService;

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

    @BeforeEach
    public void beforeEach() {

        saveDummyBruker(DUMMY_EN);
        saveDummyBruker(DUMMY_TO);
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();

        when(brukerService.fetchBrukerOrTeamBruker(anyString())).thenReturn(DUMMY_EN);
    }

    @AfterEach
    public void afterEach() {
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();

    }

    @Test
    @DisplayName("Oppretter og returnerer alle maler tilknyttet to forskjellige brukere")
    void shouldCreateAndGetMaler() throws Exception {

        var brukerEn = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var brukerTo = brukerRepository.findBrukerByBrukerId(DUMMY_TO.getBrukerId()).orElseThrow();

        var bestilling = saveDummyBestilling(brukerEn, saveDummyGruppe());
        var bestillingTo = saveDummyBestilling(brukerTo, saveDummyGruppe());

        mockMvc.perform(post("/api/v1/malbestilling")
                        .queryParam("bestillingId", String.valueOf(bestilling.getId()))
                        .queryParam("malNavn", MALNAVN_EN))
                .andExpect(status().isOk());

        when(brukerService.fetchBrukerOrTeamBruker(anyString())).thenReturn(DUMMY_TO);

        mockMvc.perform(post("/api/v1/malbestilling")
                        .queryParam("bestillingId", String.valueOf(bestillingTo.getId()))
                        .queryParam("malNavn", MALNAVN_TO))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.test_en[0].malNavn").value(MALNAVN_EN))
                .andExpect(jsonPath("$.malbestillinger.test_to[0].malNavn").value(MALNAVN_TO))
                .andExpect(jsonPath("$.malbestillinger.ALLE.length()").value(2));
    }

    @Test
    @DisplayName("Oppretter mal fra gjeldende bestilling og tester at NotFoundError blir kastet ved ugyldig bestillingId")
    void shouldCreateMalerFromExistingOrder()
            throws Exception {

        var brukerEn = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bestilling = saveDummyBestilling(brukerEn, saveDummyGruppe());

        mockMvc.perform(post("/api/v1/malbestilling")
                        .queryParam("bestillingId", String.valueOf(bestilling.getId()))
                        .queryParam("malNavn", MALNAVN_EN))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/malbestilling")
                        .queryParam("bestillingId", UGYLDIG_BESTILLINGID)
                        .queryParam("malNavn", MALNAVN_TO))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Oppretter, endrer navn på og sletter til slutt bestillingMal")
    void shouldCreateUpdateAndDeleteMal()
            throws Exception {

        var brukerEn = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bestillingMal = saveDummyBestillingMal(brukerEn);

        mockMvc.perform(put("/api/v1/malbestilling/id/{id}", bestillingMal.getId())
                        .queryParam("malNavn", NYTT_MALNAVN))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/malbestilling"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/malbestilling/id/{id}", bestillingMal.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/malbestilling"))
                .andExpect(status().isOk());

    }


    BestillingMal saveDummyBestillingMal(Bruker bruker) {
        return bestillingMalRepository.save(
                BestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .malNavn(MALNAVN_EN)
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
}