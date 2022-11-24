package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import no.nav.registre.testnorge.generersyntameldingservice.consumer.credentials.SyntAmeldingProperties;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
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

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static no.nav.registre.testnorge.generersyntameldingservice.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class SyntAmeldingConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private SyntAmeldingConsumer syntAmeldingConsumer;

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(SyntAmeldingProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void shouldGetEnkeltArbeidsforhold() {
        stubPostArbeidsforhold();

        var startdato = LocalDate.of(2020, 1, 1);

        var response = syntAmeldingConsumer.getEnkeltArbeidsforhold(
                ArbeidsforholdPeriode.builder()
                        .startdato(startdato)
                        .build(),
                ArbeidsforholdType.ordinaertArbeidsforhold);

        assertThat(response.getArbeidsforholdType()).isEqualTo(ArbeidsforholdType.ordinaertArbeidsforhold.toString());
        assertThat(response.getStartdato()).isEqualTo("2020-01-01");
    }

    private void stubPostArbeidsforhold() {
        stubFor(post(urlPathMatching("(.*)/synt/api/v1/arbeidsforhold/start/ordinaert"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/synt_arbeidsforhold.json"))
                )
        );
    }

    @Test
    void shouldGetArbeidsforholdHistorikk() {
        stubPostHistorikk();

        var response = syntAmeldingConsumer.getHistorikk(Arbeidsforhold.builder().build());

        assertThat(response).hasSize(6);
    }

    private void stubPostHistorikk() {
        stubFor(post(urlPathMatching("(.*)/synt/api/v1/arbeidsforhold"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/synt_historikk.json"))
                )
        );
    }

}
