package no.nav.dolly.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.opensearch.BestillingDokument;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@ExtendWith(TestDatabaseConfig.class)
class OpensearchControllerTest {

    private static final String BASE_URL = "/api/v1/elastic";
    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String IDENT3 = "33333333333";
    private static final String IDENT4 = "44444444444";
    private static final String IDENT5 = "55555555555";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OpenSearchService openSearchService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @BeforeEach
    void setup() {

        bestillingOpenSearchRepository.saveAll(getTestBestillinger());
    }

    @AfterEach
    void teardown() {

        bestillingOpenSearchRepository.deleteAll();
    }

    @Test
    void deleteBestilling_OK() {

        webTestClient
                .delete()
                .uri(BASE_URL + "/bestilling/id/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        var bestilling = bestillingOpenSearchRepository.findById(2L);
        assertThat(bestilling.isPresent(), is(true));

        webTestClient
                .delete()
                .uri(BASE_URL + "/bestilling/id/{id}", 2L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        bestilling = bestillingOpenSearchRepository.findById(1L);
        assertThat(bestilling.isPresent(), is(false));
    }

    @Test
    void deleteAlleBestillingerInkludertIndeks_OK() throws Exception {

        when(openSearchService.deleteIndex())
                .thenReturn(Mono.just(objectMapper.readTree("{\"acknowledged\": true}")));

        webTestClient
                .delete()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        var bestillinger = (PageImpl<BestillingDokument>) bestillingOpenSearchRepository.findAll();
        assertThat(bestillinger.getTotalElements(), is(equalTo(0L)));
    }
}