package no.nav.dolly.bestilling.instdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstdataConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENVIRONMENT = "U2";

    private InstdataConsumer instdataConsumer;

    @BeforeEach
    void setup() {

        when(consumers.getTestnavInstProxy()).thenReturn(serverProperties);
        instdataConsumer = new InstdataConsumer(tokenExchange, consumers, new ObjectMapper(), webClient);
    }

    @Test
    void deleteInstdata() {

        stubDeleteInstData();

        StepVerifier.create(instdataConsumer.deleteInstdata(List.of(IDENT)))
                .assertNext(status ->
                        assertThat(status, hasSize(1)))
                .verifyComplete();
//                .subscribe(resultat ->
//                        verify(tokenService).exchange(ArgumentMatchers.any(ServerProperties.class)));
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