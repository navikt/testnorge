package no.nav.registre.inntektsmeldingstub.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsforholdRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsgiverRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.DelvisFravaerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EierRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EndringIRefusjonRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.GraderingIForeldrePengerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.InntektsmeldingRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.NaturalytelseDetaljerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.PeriodeRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.UtsettelseAvForeldrepengerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InntektsmeldingServiceTest {

    @Mock
    ArbeidsforholdRepository arbeidsforholdRepository;
    @Mock
    ArbeidsgiverRepository arbeidsgiverRepository;
    @Mock
    private DelvisFravaerRepository delvisFravaerRepository;
    @Mock
    private GraderingIForeldrePengerRepository graderingIForeldrePengerRepository;
    @Mock
    private InntektsmeldingRepository inntektsmeldingRepository;
    @Mock
    private NaturalytelseDetaljerRepository naturalytelseDetaljerRepository;
    @Mock
    private PeriodeRepository periodeRepository;
    @Mock
    private EndringIRefusjonRepository endringIRefusjonRepository;
    @Mock
    private UtsettelseAvForeldrepengerRepository utsettelseAvForeldrepengerRepository;
    @Mock
    private EierRepository eierRepository;

    private Inntektsmelding inntekt_A, inntekt_B, inntekt_C, inntekt_D, inntekt_E, inntekt_F;

    @InjectMocks
    private InntektsmeldingService inntektsmeldingService;

    @Before
    public void setup() {

        inntekt_A = Inntektsmelding.builder().id(0L).ytelse("KREV").arbeidstakerFnr("00112233445")
                .arbeidsgiver(Arbeidsgiver.builder().id(6L).virksomhetsnummer("0000011111")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();
        inntekt_B = Inntektsmelding.builder().id(1L).ytelse("LONN").arbeidstakerFnr("55667788990")
                .arbeidsgiver(Arbeidsgiver.builder().id(7L).virksomhetsnummer("0000011111")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();
        inntekt_C = Inntektsmelding.builder().id(2L).ytelse("LONN").arbeidstakerFnr("22334455667")
                .arbeidsgiver(Arbeidsgiver.builder().id(8L).virksomhetsnummer("0000011111")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();

        inntekt_D = Inntektsmelding.builder().id(3L).ytelse("KREV").arbeidstakerFnr("00112233445")
                .privatArbeidsgiver(Arbeidsgiver.builder().id(9L).virksomhetsnummer("0000022222")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();
        inntekt_E = Inntektsmelding.builder().id(4L).ytelse("LONN").arbeidstakerFnr("55667788990")
                .privatArbeidsgiver(Arbeidsgiver.builder().id(10L).virksomhetsnummer("00000222222")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();
        inntekt_F = Inntektsmelding.builder().id(5L).ytelse("LONN").arbeidstakerFnr("22334455667")
                .privatArbeidsgiver(Arbeidsgiver.builder().id(11L).virksomhetsnummer("0000022222")
                        .inntektsmeldinger(Collections.EMPTY_LIST).build())
                .arbeidsforhold(Arbeidsforhold.builder().build()).build();

        when(inntektsmeldingRepository.save(inntekt_A)).thenReturn(inntekt_A);
        when(inntektsmeldingRepository.save(inntekt_B)).thenReturn(inntekt_B);
        when(inntektsmeldingRepository.save(inntekt_C)).thenReturn(inntekt_C);
        when(inntektsmeldingRepository.save(inntekt_D)).thenReturn(inntekt_D);
        when(inntektsmeldingRepository.save(inntekt_E)).thenReturn(inntekt_E);
        when(inntektsmeldingRepository.save(inntekt_F)).thenReturn(inntekt_F);
    }

    @Test
    public void saveXML12MeldingerTest() {

        // inntektsmeldingService.saveMeldinger(Arrays.asList(inntekt_D, inntekt_E, inntekt_F), MeldingsType.TYPE_2018_12, "test");

    }

    // Test som sjekker at ikke listen med gradering i foreldrepenger blir dobbelt så lang hvis man legger til et
    // ikke-eksisterende arbeidsforhold

    // Test som sjekker at listen med eksisterende gradering i foreldrepenger blir lagt til i listen til et oppdatert arbeidsforhold
}
