package no.nav.testnav.apps.instservice.consumer;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.instservice.consumer.credential.InstTestdataProperties;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.exception.UgyldigIdentResponseException;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static no.nav.testnav.apps.instservice.utils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class InstTestdataConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private InstTestdataConsumer instTestdataConsumer;

    private final String fnr1 = "01010101010";
    private final String miljoe = "q2";

    @Before
    public void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(InstTestdataProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void shouldGetInstitusjonsmeldingerFromInst2() {
        stubGetInstitusjonsopphold();

        var result = instTestdataConsumer.hentInstitusjonsoppholdFraInst2(miljoe, fnr1);

        assertThat(result.getQ2().get(0).getTssEksternId(), is("440"));
        assertThat(result.getQ2().get(0).getStartdato(), Matchers.equalTo(LocalDate.of(2013, 7, 3)));
        assertThat(result.getQ2().get(1).getTssEksternId(), is("441"));
        assertThat(result.getQ2().get(1).getStartdato(), Matchers.equalTo(LocalDate.of(2012, 4, 4)));
    }

    @Test(expected = UgyldigIdentResponseException.class)
    public void shouldThrowUgyldigIdentResponseExceptionOnBadRequest() {
        stubGetInstitusjonsoppholdWithBadRequest();

        instTestdataConsumer.hentInstitusjonsoppholdFraInst2(miljoe, fnr1);
    }

    @Test
    public void shouldAddInstitusjonsoppholdTilInst2() {
        var institusjonsopphold = InstitusjonsoppholdV2.builder().build();

        stubAddInstitusjonsopphold();

        var oppholdResponse = instTestdataConsumer.leggTilInstitusjonsoppholdIInst2(miljoe, institusjonsopphold);

        assertThat(oppholdResponse.getStatus(), is(HttpStatus.CREATED));
    }

    private void stubGetInstitusjonsopphold() {
        stubFor(get(urlPathMatching("(.*)/inst2/api/v1/institusjonsopphold/person"))
                .withQueryParam("environments", equalTo(miljoe))
                .withHeader("norskident", equalTo(fnr1))
                .withHeader("accept", equalTo("application/json"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("institusjonsmeldingResponse.json"))));
    }

    private void stubGetInstitusjonsoppholdWithBadRequest() {
        stubFor(get(urlPathMatching("(.*)/inst2/api/v1/institusjonsopphold/person"))
                .withHeader("accept", equalTo("application/json"))
                .withHeader("Nav-Personident", equalTo(fnr1))
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));
    }

    private void stubAddInstitusjonsopphold() {
        stubFor(post(urlPathMatching("(.*)/inst2/api/v1/institusjonsopphold/person"))
                .withQueryParam("environments", equalTo(miljoe))
                .withHeader("accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())));
    }
}