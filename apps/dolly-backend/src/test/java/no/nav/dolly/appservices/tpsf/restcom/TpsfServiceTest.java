package no.nav.dolly.appservices.tpsf.restcom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class TpsfServiceTest {

    private static final TpsfBestilling STANDARD_TPSF_BESTILLING = TpsfBestilling.builder().identtype(FNR).build();
    private static final String STANDARD_IDENT = "123";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private TpsfService tpsfService;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    public void setup() {

        WireMock.reset();

        when(tokenService.exchange(ArgumentMatchers.any(TpsForvalterenProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter() throws JsonProcessingException {

        stubPostTpsfDataReturnsOk();

        when(objectMapper.convertValue(anyList(), eq(List.class))).thenReturn(singletonList(FNR));

        List<String> response = tpsfService.opprettIdenterTpsf(STANDARD_TPSF_BESTILLING);

        assertThat(response.get(0), is(STANDARD_IDENT));
    }

    @Test
    void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException() {

        stubPostTpsfDataThrowExpection();

        Assertions.assertThrows(WebClientResponseException.BadRequest.class, () ->
                tpsfService.opprettIdenterTpsf(STANDARD_TPSF_BESTILLING));
    }

    private void stubPostTpsfDataThrowExpection() {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/personer"))
                .willReturn(badRequest()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")
                ));
    }

    private void stubPostTpsfDataReturnsOk() throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/personer"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(List.of(STANDARD_IDENT)))
                ));
    }
}