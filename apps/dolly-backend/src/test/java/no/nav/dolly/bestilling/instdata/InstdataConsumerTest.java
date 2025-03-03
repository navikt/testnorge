package no.nav.dolly.bestilling.instdata;

import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class InstdataConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENVIRONMENT = "U2";

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @MockitoBean
    @SuppressWarnings("unused")
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private InstdataConsumer instdataConsumer;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void deleteInstdata() {

        stubDeleteInstData();

        instdataConsumer.deleteInstdata(List.of(IDENT))
                .subscribe(resultat ->
                        verify(tokenService).exchange(ArgumentMatchers.any(ServerProperties.class)));
    }

    @Test
    void postInstdata() {

        stubPostInstData();

        StepVerifier.create(instdataConsumer.postInstdata(singletonList(Instdata.builder().build()), ENVIRONMENT))
                .expectNextCount(1)
                .verifyComplete();
    }

    private void stubPostInstData() {

        stubFor(post(urlPathMatching("(.*)/api/v1/institusjonsopphold/person"))
                .withQueryParam("miljoe", equalTo("U2"))
                .willReturn(ok()));
    }

    private void stubDeleteInstData() {

        stubFor(delete(urlPathMatching("(.*)/api/v1/ident/batch"))
                .withQueryParam("identer", equalTo(IDENT))
                .withQueryParam("miljoe", equalTo(ENVIRONMENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathMatching("(.*)/api/v1/miljoer"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + ENVIRONMENT + "\"]")));
    }
}