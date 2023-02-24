package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.JwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("POST /api/v1/gruppe")
@Testcontainers
@EnableAutoConfiguration
@ComponentScan("no.nav.dolly")
@AutoConfigureMockMvc(addFilters = false)
class TestgruppeControllerGetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestgruppeRepository testgruppeRepository;

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void beforeEach() {
        flyway.migrate();
        JwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @AfterEach
    public void afterEach() {
        flyway.clean();
        JwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Returnerer testgrupper tilknyttet til bruker-ID gjennom favoritter")
    void shouldGetTestgrupperWithNavIdent()
            throws Exception {

        var bruker = createBruker();
        var testgruppe1 = createTestgruppe("Gruppen er ikke en favoritt", bruker);
        var testgruppe2 = createTestgruppe("Gruppen er en favoritt", bruker);
        bruker.setFavoritter(Set.of(testgruppe2));
        bruker = brukerRepository.save(bruker);

        mockMvc
                .perform(get("/api/v1/gruppe?brukerId={brukerId}", bruker.getBrukerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.antallElementer").value(2))
                .andExpect(jsonPath("$.contents.length()").value(2))
                .andExpect(jsonPath("$.contents[?(@.favorittIGruppen == false)].navn").value(testgruppe1.getNavn()))
                .andExpect(jsonPath("$.contents[?(@.favorittIGruppen == true)].navn").value(testgruppe2.getNavn()))
                .andExpect(jsonPath("$.favoritter.length()").value(1))
                .andExpect(jsonPath("$.favoritter..navn").value(testgruppe2.getNavn()))
                .andExpect(jsonPath("$.favoritter..favorittIGruppen").value(true));

    }

    @Test
    @DisplayName("Returnerer HTTP 404 med korrekt feilmelding i body")
    void shouldFail404NotFound()
            throws Exception {

        var id = new Random().nextLong();
        mockMvc
                .perform(get("/api/v1/gruppe/{gruppeId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Gruppe med id " + id + " ble ikke funnet."));

    }

    @Test
    @DisplayName("Returnerer Testgruppe")
    void shouldReturnTestgruppe()
            throws Exception {

        var testgruppe = createTestgruppe("Testgruppe", createBruker());
        for (var i = 1; i < 11; i++) {
            createTestident("Ident " + i, testgruppe);
        }

        mockMvc
                .perform(get("/api/v1/gruppe/{gruppeId}", testgruppe.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.navn").value("Testgruppe"))
                .andExpect(jsonPath("$.antallIdenter").value(10));

    }

    private Bruker createBruker() {
        return brukerRepository.save(
                Bruker
                        .builder()
                        .brukerId("Bruker")
                        .brukertype(AZURE)
                        .build()
        );
    }

    private Testgruppe createTestgruppe(String navn, Bruker bruker) {
        return testgruppeRepository.save(
                Testgruppe
                        .builder()
                        .navn(navn)
                        .hensikt("Testing")
                        .opprettetAv(bruker)
                        .datoEndret(LocalDate.now())
                        .sistEndretAv(bruker)
                        .build()
        );
    }

    private void createTestident(String ident, Testgruppe testgruppe) {
        identRepository.save(
                Testident
                        .builder()
                        .ident(ident)
                        .testgruppe(testgruppe)
                        .master(PDL)
                        .build()
        );
    }

}

