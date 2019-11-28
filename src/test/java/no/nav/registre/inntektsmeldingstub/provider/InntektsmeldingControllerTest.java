package no.nav.registre.inntektsmeldingstub.provider;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.service.InntektsmeldingService;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsforhold;
import no.nav.registre.inntektsmeldingstub.service.rs.RsArbeidsgiver;
import no.nav.registre.inntektsmeldingstub.service.rs.RsAvsendersystem;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntekt;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.service.rs.RsKontaktinformasjon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@Profile("controllerTest")
public class InntektsmeldingControllerTest {
    @Mock
    private InntektsmeldingService inntektsmeldingService;
    private String EIER = "test";

    @InjectMocks
    private InntektsmeldingController inntektsmeldingController;

    @Test
    public void opprett201809Inntektsmelding() {
        RsInntektsmelding melding = RsInntektsmelding.builder()
                .ytelse("STATLIG").aarsakTilInnsending("KARRIEREHOPP").naerRelasjon(true).arbeidstakerFnr("11223344556")
                .avsendersystem(RsAvsendersystem.builder()
                        .innsendingstidspunkt(LocalDateTime.of(2018, 5, 15, 17, 45, 20))
                        .systemnavn("SYSTEMBOLAGET").systemversjon("42.0.1").build())
                .arbeidsgiver(RsArbeidsgiver.builder()
                        .virksomhetsnummer("112233448")
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn("GRØNN FISK").telefonnummer("87654321").build()).build())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId("76234940").foersteFravaersdag(LocalDate.of(2015, 2, 19))
                        .beregnetInntekt(RsInntekt.builder().beloep(34450.22).aarsakVedEndring("ANSIENNITET").build())
                        .build()).build();

        inntektsmeldingController.opprettInntektsmeldinger201809(EIER, Collections.singletonList(melding));
        verify(inntektsmeldingService).saveMeldinger(Collections.singletonList(melding), MeldingsType.TYPE_2018_09, EIER);
    }

    @Test
    public void fnrStoerrelseTest() {
        RsInntektsmelding melding = RsInntektsmelding.builder()
                .ytelse("STATLIG").aarsakTilInnsending("KARRIEREHOPP").naerRelasjon(true).arbeidstakerFnr("11223344556")
                .avsendersystem(RsAvsendersystem.builder()
                        .innsendingstidspunkt(LocalDateTime.of(2018, 5, 15, 17, 45, 20))
                        .systemnavn("SYSTEMBOLAGET").systemversjon("42.0.1").build())
                .arbeidsgiver(RsArbeidsgiver.builder()
                        .virksomhetsnummer("11223344")
                        .kontaktinformasjon(RsKontaktinformasjon.builder()
                                .kontaktinformasjonNavn("GRØNN FISK").telefonnummer("87654321").build()).build())
                .arbeidsforhold(RsArbeidsforhold.builder()
                        .arbeidsforholdId("76234940").foersteFravaersdag(LocalDate.of(2015, 2, 19))
                        .beregnetInntekt(RsInntekt.builder().beloep(34450.22).aarsakVedEndring("ANSIENNITET").build())
                        .build()).build();

        inntektsmeldingController.opprettInntektsmeldinger201809(EIER, Collections.singletonList(melding));
        verify(inntektsmeldingService).saveMeldinger(Collections.singletonList(melding), MeldingsType.TYPE_2018_09, EIER);
    }

    private void vanligMeldingStub() {
        String json_melding = "[{" +
                "\"ytelse\": \"STATLIG\"," +
                "\"aarsakTilInnsending\": \"KARRIEREHOPP\"," +
                "\"naerRelasjon\": true," +
                "\"arbeidstakerFnr\": \"11223344556\"," +
                "\"avsendersystem\": {" +
                "\"systemnavn\": \"SYSTEMBOLAGET\"," +
                "\"systemversjon\": \"42.0.1\"," +
                "\"innsendingstidspunkt\": \"2018-05-15T17:45:20\"" +
                "}," +
                "\"arbeidsgiver\": {" +
                "\"virksomhetsnummer\": \"112233448\"," +
                "\"kontaktinformasjon\": {" +
                "\"kontaktinformasjonNavn\": \"GRØNN FISK\"," +
                "\"telefonnummer\": \"87654321\"" +
                "}}," +
                "\"arbeidsforhold\": {" +
                "\"arbeidsforholdId\": \"76234940\"," +
                "\"foersteFravaersdag\": \"2015-02-19\"," +
                "\"beregnetInntekt\": {" +
                "\"beloep\": 34450.22," +
                "\"aarsakVedEndring\": \"ANSIENNITET\"" +
                "}}}]";
    }

}
