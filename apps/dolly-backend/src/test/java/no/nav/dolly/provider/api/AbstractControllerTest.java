package no.nav.dolly.provider.api;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static no.nav.dolly.domain.jpa.Bruker.Brukertype.AZURE;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
public abstract class AbstractControllerTest {

    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private TestgruppeRepository testgruppeRepository;
    @Autowired
    private IdentRepository identRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    public void beforeEach() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @AfterEach
    public void afterEach() {
        MockedJwtAuthenticationTokenUtils.clearJwtAuthenticationToken();
    }

    Bruker createBruker() {
        return brukerRepository.save(
                Bruker
                        .builder()
                        .brukerId(UUID.randomUUID().toString())
                        .brukertype(AZURE)
                        .brukernavn("brukernavn")
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

    Testident createTestident(String ident, Testgruppe testgruppe) {
        return identRepository.save(
                Testident
                        .builder()
                        .ident(ident)
                        .testgruppe(testgruppe)
                        .master(PDL)
                        .build()
        );
    }

}
