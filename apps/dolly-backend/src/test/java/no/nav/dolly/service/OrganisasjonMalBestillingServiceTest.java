package no.nav.dolly.service;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.OrganisasjonBestillingMalRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
class OrganisasjonMalBestillingServiceTest {

    private final static String MALNAVN = "test";
    private final static String NYTT_MALNAVN = "nyttMalnavn";
    private final static String BEST_KRITERIER = "Testeteste";
    private static final Bruker DUMMY_EN = Bruker.builder()
            .brukernavn("test_en")
            .brukerId("test_en")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_en")
            .build();
    private static final Bruker DUMMY_TO = Bruker.builder()
            .brukernavn("test_to")
            .brukerId("test_to")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_to")
            .build();

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;
    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private AzureAdTokenService azureAdTokenService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;
    @Autowired
    private OrganisasjonBestillingRepository organisasjonBestillingRepository;
    @Autowired
    private BrukerRepository brukerRepository;

    @BeforeEach
    public void beforeEach() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @AfterEach
    public void afterEach() {
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    @BeforeAll
    static void beforeAll(@Autowired BrukerRepository brukerRepository) {
        brukerRepository.save(DUMMY_EN);
        brukerRepository.save(DUMMY_TO);
    }

    @Test
    @Transactional
    @DisplayName("Oppretter og returnerer alle organisasjonmaler tilknyttet to forskjellige brukere")
    void shouldCreateAndGetMaler()
            throws Exception {

        var bruker_en = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bruker_to = brukerRepository.findBrukerByBrukerId(DUMMY_TO.getBrukerId()).orElseThrow();
        saveDummyBestillingMal(bruker_en);
        saveDummyBestillingMal(bruker_to);

        mockMvc.perform(get("/api/v1/organisasjon/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malbestillinger.test_en[0].malNavn").value(MALNAVN))
                .andExpect(jsonPath("$.malbestillinger.test_en[0].bestilling.environments", hasSize(1)))
                .andExpect(jsonPath("$.malbestillinger.test_to[0].bruker.brukerId").value(bruker_to.getBrukerId()));

        brukerRepository.deleteByBrukerId(bruker_to.getBrukerId());
    }

    @Test
    @Transactional
    @DisplayName("Oppretter mal fra gjeldende bestilling og tester at NotFoundError blir kastet ved ugyldig bestillingId")
    void shouldCreateMalerFromExistingOrder()
            throws Exception {

        var bruker_en = brukerRepository.findBrukerByBrukerId(DUMMY_EN.getBrukerId()).orElseThrow();
        var bestilling = saveDummyBestilling(bruker_en);

        mockMvc.perform(post("/api/v1/organisasjon/bestilling/malbestilling")
                        .queryParam("bestillingId", String.valueOf(bestilling.getId()))
                        .queryParam("malNavn", MALNAVN))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/organisasjon/bestilling/malbestilling")
                        .queryParam("bestillingId", "999")
                        .queryParam("malNavn", MALNAVN))
                .andExpect(status().is4xxClientError());

        brukerRepository.deleteByBrukerId(bruker_en.getBrukerId());
    }

    @Test
    @Transactional
    @DisplayName("Oppretter, endrer navn på og sletter til slutt bestillingMal")
    void shouldCreateUpdateAndDeleteMal()
            throws Exception {

        var bruker_to = brukerRepository.findBrukerByBrukerId(DUMMY_TO.getBrukerId()).orElseThrow();
        var bestillingMal = saveDummyBestillingMal(bruker_to);

        mockMvc.perform(put("/api/v1/organisasjon/bestilling/malbestilling/{id}", bestillingMal.getId())
                        .queryParam("malNavn", NYTT_MALNAVN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));

        mockMvc.perform(delete("/api/v1/organisasjon/bestilling/malbestilling/{id}", bestillingMal.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/organisasjon/bestilling/malbestilling"))
                .andExpect(status().isOk());

        brukerRepository.deleteByBrukerId(bruker_to.getBrukerId());
    }

    OrganisasjonBestillingMal saveDummyBestillingMal(Bruker bruker) {
        return organisasjonBestillingMalRepository.save(
                OrganisasjonBestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .miljoer("q2")
                        .malNavn(MALNAVN)
                        .build()
        );
    }

    OrganisasjonBestilling saveDummyBestilling(Bruker bruker) {
        return organisasjonBestillingRepository.save(
                OrganisasjonBestilling
                        .builder()
                        .ferdig(false)
                        .antall(1)
                        .miljoer("q2")
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .sistOppdatert(LocalDateTime.now())
                        .build()
        );
    }

}