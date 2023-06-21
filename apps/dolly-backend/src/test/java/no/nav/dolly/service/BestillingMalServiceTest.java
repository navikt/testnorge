package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@EnableAutoConfiguration
@ComponentScan("no.nav.dolly")
@AutoConfigureMockMvc(addFilters = false)
public class BestillingMalServiceTest {

    private final static Long BESTILLING_ID = 123L;
    private final static String MALNAVN = "test";
    private final static String BEST_KRITERIER = "Testeteste";
    private static final String MALER_ALLE = "ALLE";
    private static final Bruker BRUKER = Bruker.builder()
            .brukerId("123")
            .brukernavn("test")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test")
            .build();


    @Mock
    private MapperFacade mapperFacade;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BestillingMalRepository bestillingMalRepository;
    @Autowired
    private BestillingRepository bestillingRepository;
    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private Flyway flyway;

    @Autowired
    private BestillingMalService bestillingMalService;

    @BeforeEach
    public void beforeEach() {
        flyway.migrate();
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @AfterEach
    public void afterEach() {
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    @BeforeAll
    public static void beforeAll() {
        
    }

    @Test
    @DisplayName("Returnerer testgrupper tilknyttet til bruker-ID gjennom favoritter")
    void shouldGetEmptyMaler()
            throws Exception {

        var bruker = saveBruker(BRUKER);
        var bestillingMal = saveBestillingMal();

        mockMvc
                .perform(get("/api/v1/bestilling/malbestilling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestKriterier").value(BEST_KRITERIER))
                .andExpect(jsonPath("$.malBestillingNavn").value(2));

    }

    BestillingMal saveBestillingMal() {
        return bestillingMalRepository.save(
                BestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(BRUKER)
                        .malBestillingNavn(MALNAVN)
                        .build()
        );
    }

    Bruker saveBruker(Bruker bruker) {
        return brukerRepository.save(bruker);
    }

    Optional<BestillingMal> findMalbestillingById(Long id) {
        return bestillingMalRepository.findById(id);
    }
}
