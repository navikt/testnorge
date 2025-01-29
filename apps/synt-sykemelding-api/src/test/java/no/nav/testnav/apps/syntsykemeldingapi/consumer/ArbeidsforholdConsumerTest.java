package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.libs.DollySpringBootTest;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestArbeidsforholdDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class ArbeidsforholdConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArbeidsforholdConsumer arbeidsforholdConsumer;

    private static final String IDENT = "01019049945";
    private static final String ORGNR = "123456789";
    private static final String ARBEIDSFORHOLD_ID = "1";
    private static final String ARBEIDSFORHOLD_URL = "(.*)/arbeidsforhold/api/v1/arbeidsforhold/" + IDENT + "/" + ORGNR + "/" + ARBEIDSFORHOLD_ID;

    private ArbeidsforholdDTO arbeidsforholdResponse;

    @BeforeEach
    void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        arbeidsforholdResponse = getTestArbeidsforholdDTO(ARBEIDSFORHOLD_ID, ORGNR);
    }

    @Test
    void shouldGetArbeidsforhold() throws JsonProcessingException {
        stubArbeidsforhold();

        var response = arbeidsforholdConsumer.getArbeidsforhold(IDENT, ORGNR, ARBEIDSFORHOLD_ID);

        assertThat(response).isNotNull().isEqualTo(arbeidsforholdResponse);
    }

    private void stubArbeidsforhold() throws JsonProcessingException {
        stubFor(get(urlPathMatching(ARBEIDSFORHOLD_URL))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(arbeidsforholdResponse))));
    }

}
