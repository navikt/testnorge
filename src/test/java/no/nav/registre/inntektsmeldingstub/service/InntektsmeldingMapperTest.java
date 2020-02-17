package no.nav.registre.inntektsmeldingstub.service;

import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
public class InntektsmeldingMapperTest {
    private RsInntektsmelding minimalMelding;
    private RsInntektsmelding fullMelding;
    private RsInntektsmelding privatArbeidsgiverMelding;

    @Before
    public void setup() {
         minimalMelding = InntektsmeldingFactory.getMinimalMelding();
         fullMelding = InntektsmeldingFactory.getFullMelding();
         privatArbeidsgiverMelding = InntektsmeldingFactory.getPrivatArbeidsgiverMelding();
    }

    @Test
    public void tomMeldingTest() {
        Inntektsmelding i = RestToDBMapper.map201809melding(null).build();
        assertThat(Objects.isNull(i.getPrivatArbeidsgiver()), is(true));
        assertThat(i.getArbeidstakerFnr() == null, is(true));
    }

    @Test
    public void minimalMeldingTest() {
        Inntektsmelding i = RestToDBMapper.map201809melding(minimalMelding).build();
        assertThat(i.getArbeidstakerFnr(), is("11223344556"));
        assertThat(i.getArbeidsgiver().isPresent(), is(true));
    }

    @Test
    public void fullMeldingTest() {
        Inntektsmelding i = RestToDBMapper.map201812melding(fullMelding).build();
        assertThat(i.getGjenopptakelseNaturalytelseListe().size(), is(2));
        assertThat(i.getSykepengerPerioder().size(), is(2));
        assertThat(i.getArbeidsgiver().isPresent(), is(true));
        assertThat(i.getArbeidsgiver().get().getTelefonnummer(), is("81549300"));
        assertThat(Objects.isNull(i.getPrivatArbeidsgiver()), is(true));
    }

    @Test
    public void privatArbeidsgiverTest() {
        Inntektsmelding i = RestToDBMapper.map201812melding(privatArbeidsgiverMelding).build();
        assertThat(i.getArbeidsgiver().isPresent(), is(true));
        assertThat(i.getPrivatArbeidsgiver().isPresent(), is(true));
        assertThat(i.getArbeidsgiver().get().getVirksomhetsnummer().length(), is(11));
        assertThat(i.getArbeidsgiver().get() == i.getPrivatArbeidsgiver().get(), is(true));
    }

}
