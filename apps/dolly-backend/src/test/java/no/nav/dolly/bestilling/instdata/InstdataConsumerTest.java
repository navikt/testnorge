package no.nav.dolly.bestilling.instdata;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.domain.resultset.inst.Instdata;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;

class InstdataConsumerTest extends AbstractConsumerTest {

        private static final String IDENT = "12345678901";
        private static final String ENVIRONMENT = "U2";

        @Autowired
        private InstdataConsumer instdataConsumer;

        @Test
        void deleteInstdata() {

            stubDeleteInstData();

            instdataConsumer.deleteInstdata(List.of(IDENT))
                    .as(StepVerifier::create)
                    .assertNext(status ->
                            assertThat(status.getFirst().getStatus(), Matchers.is(HttpStatus.OK)))
                    .verifyComplete();
        }

        @Test
        void postInstdata() {

            stubPostInstData();

            instdataConsumer.postInstdata(singletonList(Instdata.builder().build()), ENVIRONMENT)
                    .as(StepVerifier::create)
                    .assertNext(status ->
                            MatcherAssert.assertThat(status.getStatus(), Matchers.is(HttpStatus.OK)))
                    .verifyComplete();
        }

        private void stubPostInstData() {

            stubFor(post(urlPathMatching("(.*)/api/v1/institusjonsopphold/person"))
                    .withQueryParam("environments", equalTo(ENVIRONMENT))
                    .willReturn(ok()));
        }

        private void stubDeleteInstData() {

            stubFor(get(urlPathMatching("(.*)/api/v1/environment"))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"institusjonsoppholdEnvironments\":[\"" + ENVIRONMENT + "\"]}")));

            stubFor(delete(urlPathMatching("(.*)/api/v1/institusjonsopphold/person"))
                    .withQueryParam("environments", equalTo(ENVIRONMENT))
                    .withHeader("norskident", equalTo(IDENT))
                    .willReturn(ok()
                            .withHeader("Content-Type", "application/json")));

        }
}