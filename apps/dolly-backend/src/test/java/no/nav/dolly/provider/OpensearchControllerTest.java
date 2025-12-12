package no.nav.dolly.provider;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.opensearch.BestillingDokument;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DollySpringBootTest
@Import(TestDatabaseConfig.class)
class OpensearchControllerTest {

    private static final String BASE_URL = "/api/v1/opensearch";
    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String IDENT3 = "33333333333";
    private static final String IDENT4 = "44444444444";
    private static final String IDENT5 = "55555555555";
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private OpenSearchService openSearchService;

    private List<BestillingDokument> getTestBestillinger() {

        return List.of(
                BestillingDokument.builder()
                        .id(1L)
                        .pdldata(PdlPersondata.builder()
                                .opprettNyPerson(PdlPersondata.PdlPerson.builder()
                                        .identtype(Identtype.FNR)
                                        .syntetisk(true)
                                        .build())
                                .build())
                        .bankkonto(BankkontoData.builder()
                                .utenlandskBankkonto(BankkontonrUtlandDTO.builder()
                                        .tilfeldigKontonummer(true)
                                        .build())
                                .build())
                        .identer(List.of(IDENT1, IDENT2, IDENT3))
                        .build(),
                BestillingDokument.builder()
                        .id(2L)
                        .pdldata(PdlPersondata.builder()
                                .opprettNyPerson(PdlPersondata.PdlPerson.builder()
                                        .identtype(Identtype.FNR)
                                        .syntetisk(true)
                                        .build())
                                .build())
                        .aareg(List.of(RsAareg.builder()
                                .arbeidsforholdstype("forenkletOppgjoersordning")
                                .ansettelsesPeriode(RsAnsettelsesPeriode.builder()
                                        .fom(LocalDateTime.of(2003, 10, 20, 0, 0))
                                        .build())
                                .arbeidsavtale(RsArbeidsavtale.builder()
                                        .yrke("2521106")
                                        .build())
                                .arbeidsgiver(RsOrganisasjon.builder()
                                        .aktoertype("ORG")
                                        .orgnummer("896929119")
                                        .build())
                                .build()))
                        .identer(List.of(IDENT4, IDENT5))
                        .build());
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + TestDatabaseConfig.POSTGRES.getMappedPort(5432) + "/test");
        registry.add("spring.r2dbc.username", TestDatabaseConfig.POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", TestDatabaseConfig.POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @BeforeEach
    void setup() {
        openSearchService.createIndex()
                .block();

        openSearchService.saveAll(getTestBestillinger())
                .block();
    }

    @AfterEach
    void teardown() {

        openSearchService.deleteIndex()
                .block();
    }

    @Disabled("Flaky test - needs investigation of OpenSearch testcontainer behavior")
    @Test
    void deleteBestilling_OK() {

        webTestClient
                .delete()
                .uri(BASE_URL + "/bestilling/id/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        var exists = openSearchService.exists(2L).block();
        assertThat(exists, is(true));

        webTestClient
                .delete()
                .uri(BASE_URL + "/bestilling/id/{id}", 2L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        exists = openSearchService.exists(1L).block();
        assertThat(exists, is(false));
    }

    @Test
    void deleteIndeks_OK() {

        webTestClient
                .delete()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        var exists = openSearchService.exists(1L).block();
        assertThat(exists, is(false));

        exists = openSearchService.exists(2L).block();
        assertThat(exists, is(false));
    }
}