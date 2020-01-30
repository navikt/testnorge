package no.nav.registre.inntektsmeldingstub.service;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;
import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;
import no.nav.registre.inntektsmeldingstub.database.model.Eier;
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
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

// @AutoConfigureTestDatabase
@DataJpaTest
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
//@SpringBootTest
//@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class InntektsmeldingServiceTest {

    @Autowired
    private ArbeidsforholdRepository arbeidsforholdRepository;
    @Autowired
    private ArbeidsgiverRepository arbeidsgiverRepository;
    @Autowired
    private DelvisFravaerRepository delvisFravaerRepository;
    @Autowired
    private GraderingIForeldrePengerRepository graderingIForeldrePengerRepository;
    @Autowired
    private InntektsmeldingRepository inntektsmeldingRepository;
    @Autowired
    private NaturalytelseDetaljerRepository naturalytelseDetaljerRepository;
    @Autowired
    private PeriodeRepository periodeRepository;
    @Autowired
    private EndringIRefusjonRepository endringIRefusjonRepository;
    @Autowired
    private UtsettelseAvForeldrepengerRepository utsettelseAvForeldrepengerRepository;
    @Autowired
    private EierRepository eierRepository;

    private Inntektsmelding inntekt_A, inntekt_B, inntekt_C, inntekt_D, inntekt_E, inntekt_F;
    private InntektsmeldingService inntektsmeldingService;

    @Before
    public void setup() {
        inntektsmeldingService = new InntektsmeldingService(
                arbeidsforholdRepository,
                arbeidsgiverRepository,
                delvisFravaerRepository,
                graderingIForeldrePengerRepository,
                inntektsmeldingRepository,
                naturalytelseDetaljerRepository,
                periodeRepository,
                endringIRefusjonRepository,
                utsettelseAvForeldrepengerRepository,
                eierRepository);

        inntekt_A = Inntektsmelding.builder()
                .ytelse("miniytelse")
                .aarsakTilInnsending("minitesting")
                .arbeidsgiver(Arbeidsgiver.builder()
                        .virksomhetsnummer("123456789")
                        .telefonnummer("12345678")
                        .kontaktinformasjonNavn("Bjarne Bjarne")
                        .build())
                .arbeidsforhold(Arbeidsforhold.builder()
                        .arbeidforholdsId("ANSWER42")
                        .build())
                .build();
    }

    @Test
    public void saveXML12MeldingerTest() {

        RsInntektsmelding melding = InntektsmeldingFactory.getMinimalMelding();
        List<Inntektsmelding> res = inntektsmeldingService.saveMeldinger(Collections.singletonList(melding),
                MeldingsType.TYPE_2018_12, "test");
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getEier().getNavn(), is("test"));
    }

    @Test
    public void save2MeldingerSammeArbGiverTest() {
        RsInntektsmelding fullMelding = InntektsmeldingFactory.getFullMelding();
        RsInntektsmelding miniMelding = InntektsmeldingFactory.getMinimalMelding();

        List<Inntektsmelding> res = inntektsmeldingService.saveMeldinger(Collections.singletonList(miniMelding),
                MeldingsType.TYPE_2018_12, "test");
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getEier().getNavn(), is("test"));
        assertThat(res.get(0).getArbeidsgiver().get().getInntektsmeldinger().size(), is(1));



        res = inntektsmeldingService.saveMeldinger(Collections.singletonList(fullMelding),
                MeldingsType.TYPE_2018_12, "test");

        assertThat(res.get(0).getArbeidsgiver().get().getInntektsmeldinger().size(), is(2));

        List<Eier> e = (List<Eier>) eierRepository.findAll();
        assertThat(e.size(), is(1));

    }

    @Test
    public void save2MeldingerPrivatArbeidsgiver() {

    }

    @Test
    public void save2ArbeidsforholdSammeArbeidsgiver() {

    }

    // Test som sjekker at ikke listen med gradering i foreldrepenger blir dobbelt så lang hvis man legger til et
    // ikke-eksisterende arbeidsforhold

    // Test som sjekker at listen med eksisterende gradering i foreldrepenger blir lagt til i listen til et oppdatert arbeidsforhold
}
