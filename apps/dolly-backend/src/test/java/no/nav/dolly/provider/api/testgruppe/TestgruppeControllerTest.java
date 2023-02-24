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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@EnableAutoConfiguration
@ComponentScan("no.nav.dolly")
@AutoConfigureMockMvc(addFilters = false)
public abstract class TestgruppeControllerTest {

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private TestgruppeRepository testgruppeRepository;

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

    Bruker createBruker() {
        return brukerRepository.save(
                Bruker
                        .builder()
                        .brukerId("Bruker")
                        .brukertype(AZURE)
                        .build()
        );
    }

    Bruker saveBruker(Bruker bruker) {
        return brukerRepository.save(bruker);
    }

    Testgruppe createTestgruppe(String navn, Bruker bruker) {
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

    Optional<Testgruppe> findTestgruppeById(Long id) {
        return testgruppeRepository.findById(id);
    }

    void createTestident(String ident, Testgruppe testgruppe) {
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
