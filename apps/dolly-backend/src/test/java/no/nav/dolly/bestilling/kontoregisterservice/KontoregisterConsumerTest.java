package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class KontoregisterConsumerTest {
    private static final String IDENT = "12345678901";
    private static final String KONTONUMMER = "1234567890";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private KontoregisterConsumer kontoregisterConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(KontoregisterConsumerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @AfterEach
    public void cleanUp() {
        WireMock.getAllServeEvents()
                .stream().forEach(e -> WireMock.removeServeEvent(e.getId()));
    }

    private String hentKontoResponse() {
        return "{\n" +
                "    \"aktivKonto\": {\n" +
                "        \"kontohaver\": \"" + IDENT + "\",\n" +
                "        \"kontonummer\": \"" + KONTONUMMER + "\",\n" +
                "        \"gyldigFom\": \"2022-08-16T14:15:05.573486\",\n" +
                "        \"opprettetAv\": \"Dolly\",\n" +
                "        \"utenlandskKontoInfo\": {\n" +
                "            \"banknavn\": \"string\",\n" +
                "            \"bankkode\": \"XXXX\",\n" +
                "            \"bankLandkode\": \"SE\",\n" +
                "            \"valutakode\": \"SEK\",\n" +
                "            \"swiftBicKode\": \"SHEDSE22\",\n" +
                "            \"bankadresse1\": \"string\",\n" +
                "            \"bankadresse2\": \"string\",\n" +
                "            \"bankadresse3\": \"string\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @Test
    void opprettBankkonto_OK() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/oppdater-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        var response = kontoregisterConsumer.postKontonummerRegister(new OppdaterKontoRequestDTO())
                .block();

        assertThat(response, is(equalTo("OK")));
    }

    @Test
    void opprettBankkonto_feil() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/oppdater-konto"))
                        .willReturn(badRequest()
                                .withBody("{\"feilmelding\":\"Noe galt har skjedd\"}")
                                .withHeader("Content-Type", "application/json")));

        var response = kontoregisterConsumer.postKontonummerRegister(new OppdaterKontoRequestDTO())
                .block();

        assertThat(response, is(equalTo("Feil= Noe galt har skjedd")));
    }

    @Test
    void slettBankkonto_OK() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/slett-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        var response = kontoregisterConsumer.deleteKontonumre(List.of(IDENT))
                .block();

        assertThat(response, is(empty()));
    }

    @Test
    void testHentBankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/hent-konto"))
                        .willReturn(ok()
                                .withBody(hentKontoResponse())
                                .withHeader("Content-Type", "application/json")));

        var hentResponse = kontoregisterConsumer.getKontonummer(IDENT).block();

        var hentBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, HentKontoRequestDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        hentBankkontoer.stream()
                .forEach(b -> {
                    assertThat("sendt ident er riktig", IDENT.equals(b.getKontohaver()));
                });

        assertThat(hentResponse.getAktivKonto().getKontonummer(), is(equalTo(KONTONUMMER)));
        assertThat(hentResponse.getAktivKonto().getKontohaver(), is(equalTo(IDENT)));
        assertThat(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getBankkode(), is(equalTo("XXXX")));
        assertThat(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getSwiftBicKode(), is(equalTo("SHEDSE22")));
    }
}
