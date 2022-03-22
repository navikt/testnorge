package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PensjonTestdataFacadeProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class PensjonTestdataFacadeConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private PensjonTestdataFacadeConsumer pensjonConsumer;

    private static final PensjonTestdataPerson PERSON = PensjonTestdataPerson.builder()
            .fnr("12345678910")
            .build();
    private static final PensjonTestdataInntekt INNTEKT = PensjonTestdataInntekt.builder()
            .fnr("12345678910")
            .belop(123456)
            .build();

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(PensjonTestdataFacadeProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }


    @Test
    void shouldOpprettPerson() {
        stubOpprettPerson();

        var response = pensjonConsumer.opprettPerson(PERSON);
        assertThat(response.getStatus()).hasSize(1);
        assertThat(response.getStatus().get(0).getMiljo()).isEqualTo("q2");
    }

    private void stubOpprettPerson() {
        stubFor(post(urlPathMatching("(.*)/pensjon/api/v1/person"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/pensjon/pensjon_response.json")))
        );
    }

    @Test
    void shouldHandleErrorOpprettPerson() {
        stubErrorOpprettPerson();

        var response = pensjonConsumer.opprettPerson(PERSON);
        assertThat(response.getStatus()).isEmpty();
    }

    private void stubErrorOpprettPerson() {
        stubFor(post(urlPathMatching("(.*)/pensjon/api/v1/person"))
                .willReturn(aResponse().withStatus(500))
        );
    }

    @Test
    void shouldOpprettInntetk() {
        stubOpprettInntekt();

        var response = pensjonConsumer.opprettInntekt(INNTEKT);
        assertThat(response.getStatus()).hasSize(1);
        assertThat(response.getStatus().get(0).getMiljo()).isEqualTo("q2");
    }

    private void stubOpprettInntekt() {
        stubFor(post(urlPathMatching("(.*)/pensjon/api/v1/inntekt"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/pensjon/pensjon_response.json")))
        );
    }


    @Test
    void shouldHandleErrorOpprettInntekt() {
        stubErrorOpprettInntekt();

        var response = pensjonConsumer.opprettInntekt(INNTEKT);
        assertThat(response.getStatus()).isEmpty();
    }

    private void stubErrorOpprettInntekt() {
        stubFor(post(urlPathMatching("(.*)/pensjon/api/v1/inntekt"))
                .willReturn(aResponse().withStatus(500))
        );
    }
}
