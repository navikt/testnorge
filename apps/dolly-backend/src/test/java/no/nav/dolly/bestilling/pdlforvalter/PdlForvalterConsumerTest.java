package no.nav.dolly.bestilling.pdlforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class PdlForvalterConsumerTest {

    private static final String IDENT = "11111111111";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private PdlForvalterConsumer pdlForvalterConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.generateToken(ArgumentMatchers.any(PdlProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/kontaktinformasjonfordoedsbo"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder()
                .build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void postUtenlandskIdentifikasjonsnummer_OK() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/utenlandsidentifikasjonsnummer"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder()
                .build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void postFalskIdenitet_OK() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/falskidentitet"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postFalskIdentitet(PdlFalskIdentitet.builder()
                .build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void deleteIdent() {

        stubFor(delete(urlPathMatching("(.*)/api/v1/personident"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.deleteIdent(IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void opprettPerson() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/opprettperson"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postOpprettPerson(PdlOpprettPerson.builder()
                .opprettetIdent(IDENT)
                .build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void opprettPersonMedIdentHistorikk() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/opprettperson"))
                .withQueryParam("historiskePersonidenter", matching("Person(1|2)"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        List<String> identHistorikkList = new ArrayList<>();
        identHistorikkList.add("Person1");
        identHistorikkList.add("Person2");

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postOpprettPerson(PdlOpprettPerson.builder()
                .opprettetIdent(IDENT)
                .historiskeIdenter(identHistorikkList)
                .build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void leggTillNavn() {

        stubFor(post(urlPathMatching("(.*)/api/v1/bestilling/navn"))
                .withHeader(HEADER_NAV_PERSON_IDENT, equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        ResponseEntity<JsonNode> response = pdlForvalterConsumer.postNavn(PdlNavn.builder().build(), IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }
}