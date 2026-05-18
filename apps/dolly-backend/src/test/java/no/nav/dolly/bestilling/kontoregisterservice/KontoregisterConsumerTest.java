package no.nav.dolly.bestilling.kontoregisterservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.testnav.libs.dto.kontoregister.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.KontoregisterResponseDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class KontoregisterConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String KONTONUMMER = "1234567890";
    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    @Autowired
    private KontoregisterConsumer kontoregisterConsumer;

    private static String hentKontoResponse() {
        return "{\n" +
                "    \"kontohaver\": \"" + IDENT + "\",\n" +
                "    \"kontonummer\": \"" + KONTONUMMER + "\",\n" +
                "    \"gyldigFom\": \"2022-08-16T14:15:05.573486\",\n" +
                "    \"opprettetAv\": \"Dolly\",\n" +
                "    \"utenlandskKontoInfo\": {\n" +
                "        \"banknavn\": \"string\",\n" +
                "        \"bankkode\": \"XXXX\",\n" +
                "        \"bankLandkode\": \"SE\",\n" +
                "        \"valutakode\": \"SEK\",\n" +
                "        \"swiftBicKode\": \"SHEDSE22\",\n" +
                "        \"bankadresse1\": \"string\",\n" +
                "        \"bankadresse2\": \"string\",\n" +
                "        \"bankadresse3\": \"string\"\n" +
                "    }\n" +
                "}";
    }

    @AfterEach
    void cleanUp() {
        WireMock
                .getAllServeEvents()
                .forEach(e -> WireMock.removeServeEvent(e.getId()));
    }

    @Test
    void opprettBankkonto_OK() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/oppdater-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        StepVerifier.create(kontoregisterConsumer.opprettKontonummer(new OppdaterKontoRequestDTO()))
                .expectNext(KontoregisterResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .build())
                .verifyComplete();
    }

    @Test
    void opprettBankkonto_feil() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/oppdater-konto"))
                        .willReturn(badRequest()
                                .withBody("{\"feilmelding\":\"Noe galt har skjedd\"}")
                                .withHeader("Content-Type", "application/json")));

        StepVerifier.create(kontoregisterConsumer.opprettKontonummer(new OppdaterKontoRequestDTO()))
                .expectNext(KontoregisterResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .feilmelding("{\"feilmelding\":\"Noe galt har skjedd\"}")
                        .build())
                .verifyComplete();
    }

    @Test
    void slettBankkonto_OK() {

        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/slett-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        StepVerifier.create(kontoregisterConsumer.deleteKontonumre(List.of(IDENT))
                        .collectList())
                .expectNext(List.of(KontoregisterResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .build()))
                .verifyComplete();
    }

    @Test
    void testHentBankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/hent-aktiv-konto"))
                        .willReturn(ok()
                                .withBody(hentKontoResponse())
                                .withHeader("Content-Type", "application/json")));

        var hentResponse = kontoregisterConsumer.getKontonummer(IDENT).block();

        var hentBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> objectMapper.readValue(s, HentKontoRequestDTO.class))
                .toList();

        hentBankkontoer
                .forEach(b ->
                        assertThat("sendt ident er riktig", IDENT.equals(b.getKontohaver())));

        assertThat(hentResponse.getAktivKonto().getKontonummer(), is(equalTo(KONTONUMMER)));
        assertThat(hentResponse.getAktivKonto().getKontohaver(), is(equalTo(IDENT)));
        assertThat(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getBankkode(), is(equalTo("XXXX")));
        assertThat(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getSwiftBicKode(), is(equalTo("SHEDSE22")));
    }
}
