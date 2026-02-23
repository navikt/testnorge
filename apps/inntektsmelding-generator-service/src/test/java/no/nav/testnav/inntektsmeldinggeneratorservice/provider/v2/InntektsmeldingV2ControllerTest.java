package no.nav.testnav.inntektsmeldinggeneratorservice.provider.v2;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.inntektsmeldinggeneratorservice.SecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@DollySpringBootTest
@Import(SecurityTestConfig.class)
class InntektsmeldingV2ControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreate() {
        String requestBody = "{"
                + "\"ytelse\":\"Sykepenger\","
                + "\"aarsakTilInnsending\":\"Ny\","
                + "\"arbeidstakerFnr\":\"12345678912\","
                + "\"naerRelasjon\":false,"
                + "\"avsendersystem\":{\"systemnavn\":\"Dolly\",\"systemversjon\":\"2.0\",\"innsendingstidspunkt\":\"2024-06-05T09:54:38\"},"
                + "\"arbeidsgiver\":{\"virksomhetsnummer\":\"123123123\",\"kontaktinformasjon\":{\"kontaktinformasjonNavn\":\"Dolly Dollesen\",\"telefonnummer\":\"99999999\"}},"
                + "\"arbeidsforhold\":{\"arbeidsforholdId\":\"\",\"beregnetInntekt\":{\"beloep\":23312.0},\"avtaltFerieListe\":[],\"utsettelseAvForeldrepengerListe\":[],\"graderingIForeldrepengerListe\":[]},"
                + "\"refusjon\":{\"endringIRefusjonListe\":[]},"
                + "\"sykepengerIArbeidsgiverperioden\":{},"
                + "\"opphoerAvNaturalytelseListe\":[],"
                + "\"gjenopptakelseNaturalytelseListe\":[],"
                + "\"pleiepengerPerioder\":[]"
                + "}";

        webTestClient.post()
                .uri("/api/v2/inntektsmelding/2018/12/11")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk();
    }
}