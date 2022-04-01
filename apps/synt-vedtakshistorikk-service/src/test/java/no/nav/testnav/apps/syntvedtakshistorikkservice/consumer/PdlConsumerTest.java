package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PdlProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
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

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService.SYNT_TAGS;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class PdlConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private PdlProxyConsumer pdlProxyConsumer;


    @Test
    void shouldGetPdlPerson() {
        stubTokenRequest();
        stubPdlPersonResponse();

        var response = pdlProxyConsumer.getPdlPerson("12345678910").getData();
        var ident = response.getHentIdenter().getIdenter().stream()
                .filter(identer -> identer.getGruppe().equals(PdlPerson.Gruppe.FOLKEREGISTERIDENT))
                .toList().get(0).getIdent();

        assertThat(ident).isEqualTo("12345678910");
        assertThat(response.getHentPerson().getBostedsadresse()).hasSize(1);
        assertThat(response.getHentPerson().getNavn()).hasSize(1);
    }

    private void stubPdlPersonResponse() {
        stubFor(post(urlPathMatching("(.*)/pdl/pdl-api/graphql"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/pdl/pdlperson.json")))
        );
    }

    @Test
    void shouldNotGetPdlPerson() {
        var response = pdlProxyConsumer.getPdlPerson(null);
        assertThat(response).isNull();
    }

    @Test
    void shouldHandleErrorResponse() {
        stubTokenRequest();
        stubPdlPersonErrorResponse();

        var response = pdlProxyConsumer.getPdlPerson("12345678910");
        assertThat(response).isNull();
    }

    private void stubPdlPersonErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/pdl/pdl-api/graphql"))
                .willReturn(aResponse().withStatus(500))
        );
    }

    @Test
    void shouldOpprettTags() {
        stubTokenRequest();
        stubOpprettTags();

        var response = pdlProxyConsumer.createTags(Collections.singletonList("12345678910"), SYNT_TAGS);
        assertThat(response).isTrue();
    }

    @Test
    void shouldNotOppretteTags() {
        var response = pdlProxyConsumer.createTags(null, SYNT_TAGS);
        assertThat(response).isFalse();
    }

    private void stubOpprettTags() {
        stubFor(post(urlEqualTo("/pdl/pdl-testdata/api/v1/bestilling/tags?tags=DOLLY&tags=ARENASYNT"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                )
        );
    }

    private void stubTokenRequest() {
        when(tokenExchange.exchange(ArgumentMatchers.any(PdlProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }
}
