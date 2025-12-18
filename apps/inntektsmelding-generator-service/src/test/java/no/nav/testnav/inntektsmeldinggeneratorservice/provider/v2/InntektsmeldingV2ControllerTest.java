package no.nav.testnav.inntektsmeldinggeneratorservice.provider.v2;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InntektsmeldingV2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        assertDoesNotThrow(() -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/inntektsmelding/2018/12/11")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }, "Forventer at ingen JAXBException blir kastet under konvertering til xml");
    }
}