package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.TagsService.SYNT_TAGS;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
class PdlConsumerTest {

    private static final String IDENT = "12345678910";
    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;
    @Autowired
    private PdlProxyConsumer pdlProxyConsumer;

    private void stubPdlPersonResponse() {
        stubFor(post(urlPathMatching("(.*)/pdl-api/graphql"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/pdl/pdlperson.json")))
        );
    }

    private void stubPdlPersonBolkResponse() {
        stubFor(post(urlPathMatching("(.*)/pdl-api/graphql"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/pdl/pdlpersonbolk.json")))
        );
    }

    private void stubPdlPersonErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/pdl-api/graphql"))
                .willReturn(aResponse().withStatus(500))
        );
    }

    private void stubOpprettTags() {
        stubFor(post(urlEqualTo("/pdl-testdata/api/v1/bestilling/tags?tags=ARENASYNT"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                )
        );
    }

    private void stubTokenRequest() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void shouldGetPdlPerson() {
        stubTokenRequest();
        stubPdlPersonResponse();

        var response = pdlProxyConsumer.getPdlPerson(IDENT).getData();
        var ident = response.getHentIdenter().getIdenter().stream()
                .filter(identer -> identer.getGruppe().equals(PdlPerson.Gruppe.FOLKEREGISTERIDENT))
                .toList().get(0).getIdent();

        assertThat(ident).isEqualTo(IDENT);
        assertThat(response.getHentPerson().getBostedsadresse()).hasSize(1);
        assertThat(response.getHentPerson().getNavn()).hasSize(1);
    }

    @Test
    void shouldNotGetPdlPerson() {
        var response = pdlProxyConsumer.getPdlPerson(null);
        assertThat(response).isNull();
    }

    @Test
    void shouldGetPdlPersoner() {
        stubTokenRequest();
        stubPdlPersonBolkResponse();

        var response = pdlProxyConsumer.getPdlPersoner(Collections.singletonList(IDENT)).getData();
        var bolk = response.getHentPersonBolk();
        var ident = bolk.get(0).getIdent();

        assertThat(ident).isEqualTo(IDENT);
        assertThat(bolk).hasSize(1);
    }

    @Test
    void shouldNotGetPdlPersoner() {
        var response = pdlProxyConsumer.getPdlPersoner(null);
        assertThat(response).isNull();
    }

    @Test
    void shouldHandleErrorResponse() {
        stubTokenRequest();
        stubPdlPersonErrorResponse();

        var response = pdlProxyConsumer.getPdlPerson("12345678910");
        assertThat(response).isNull();
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

}
