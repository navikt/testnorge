package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import no.nav.dolly.config.credentials.SigrunstubProxyProperties;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class SigrunStubConsumerTest {

    private static final String IDENT = "111111111";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private OpprettSkattegrunnlag skattegrunnlag;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    public void setup() {

        WireMock.reset();

        when(tokenService.exchange(ArgumentMatchers.any(SigrunstubProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        skattegrunnlag = OpprettSkattegrunnlag.builder()
                .inntektsaar("1978")
                .build();
    }

    @Test
    public void createSkattegrunnlag() {

        stubOpprettSkattegrunnlagOK();

        ResponseEntity<SigrunResponse> response = sigrunStubConsumer.createSkattegrunnlag(singletonList(this.skattegrunnlag));

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void createSkattegrunnlag_kasterSigrunExceptionHvisKallKasterClientException() throws Exception {

        stubOpprettSkattegrunnlagMedBadRequest();

        Assertions.assertThrows(WebClientResponseException.class, () ->
                sigrunStubConsumer.createSkattegrunnlag(singletonList(skattegrunnlag)));
    }

    @Test
    public void deleteSkattegrunnlag_Ok() {

        stubDeleteSkattegrunnlagOK();

        sigrunStubConsumer.deleteSkattegrunnlag(List.of(IDENT)).block();
    }

    private void stubOpprettSkattegrunnlagOK() {

        stubFor(post(urlPathMatching("(.*)/sigrunstub/api/v1/lignetinntekt"))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubOpprettSkattegrunnlagMedBadRequest() throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/sigrunstub/api/v1/lignetinntekt"))
                .willReturn(badRequest()
                        .withBody(asJsonString(singletonList(skattegrunnlag)))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubDeleteSkattegrunnlagOK() {

        stubFor(delete(urlPathMatching("(.*)/sigrunstub/api/v1/slett"))
                .withHeader("personidentifikator", matching(IDENT))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));
    }
}