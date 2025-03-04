package no.nav.dolly.bestilling.krrstub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.CommonKeysAndUtils;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class KrrstubConsumerTest {

    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final String IDENT = "12345678901";
    private static final boolean RESERVERT = true;

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private KrrstubConsumer krrStubConsumer;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void createDigitalKontaktdata_Ok() {

        stubPostKrrData();

        StepVerifier.create(krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                        .epost(EPOST)
                        .mobil(MOBIL)
                        .reservert(RESERVERT)
                        .build()))
                .expectNext(DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.OK)
                        .build())
                .verifyComplete();
    }

    @Test
    void deleteDigitalKontaktdata_Ok() {

        stubDeleteKrrData();

        StepVerifier.create(krrStubConsumer.deleteKontaktdata(List.of(IDENT)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteDigitalKontaktdataPerson_Ok() {

        var deleteStub = stubDeleteKrrData();

        StepVerifier.create(krrStubConsumer.deleteKontaktdataPerson(IDENT))
                .expectNextCount(1)
                .verifyComplete();

        var deleteEvents = WireMock.getAllServeEvents().stream()
                .filter(e -> e.getStubMapping().getId().equals(deleteStub.getId()))
                .toList();

        assertThat(deleteEvents.size(), is(equalTo(1)));
    }

    @Test
    void createDigitalKontaktdata_GenerateTokenFailed_NoAction() {

        when(tokenService.exchange(any(ServerProperties.class))).thenReturn(Mono.empty());

        StepVerifier.create(krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                        .epost(EPOST)
                        .mobil(MOBIL)
                        .reservert(RESERVERT)
                        .build()))
                .expectNextCount(0)
                .verifyComplete();

        verify(tokenService).exchange(any(ServerProperties.class));
    }

    @Test
    void feilmeldingVedHttp409() {

        stubPostKrrDataMed409();

        StepVerifier.create(krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                        .epost(EPOST)
                        .mobil(MOBIL)
                        .reservert(RESERVERT)
                        .build()))
                .expectNext(DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.CONFLICT)
                        .melding("")
                        .build())
                .verifyComplete();
    }

    private void stubPostKrrData() {

        stubFor(post(urlPathMatching("(.*)/api/v2/kontaktinformasjon"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubPostKrrDataMed409() {

        stubFor(post(urlPathMatching("(.*)/api/v2/kontaktinformasjon"))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")));
    }

    private StubMapping stubDeleteKrrData() {

        stubFor(delete(urlPathMatching("(.*)/api/v2/kontaktinformasjon/" + IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        var deletStub = stubFor(delete(urlPathMatching("(.*)/api/v2/person/kontaktinformasjon"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathMatching("(.*)/api/v2/person/kontaktinformasjon"))
                .withHeader(CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT, WireMock.equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1}")));

        return deletStub;
    }
}
