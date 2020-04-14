package no.nav.registre.testnorge.elsam.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Random;

import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.AnsettelsesPeriode;

@RunWith(MockitoJUnitRunner.class)
public class DatoUtilTest {

    @Mock
    private Random rand;

    @InjectMocks
    private DatoUtil datoUtil;

    @Test
    public void shouldLageTilfeldigDato() {
        AnsettelsesPeriode ansettelsesPeriode = AnsettelsesPeriode.builder()
                .fom("2008-10-01T00:00:00")
                .tom("2012-10-01T00:00:00")
                .build();

        LocalDate result = datoUtil.lagTilfeldigDatoIAnsettelsesperiode(ansettelsesPeriode);

        assertThat(result, equalTo(LocalDate.of(2008, 10, 1)));
    }

    @Test
    public void extractDateFromFnr1900Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("13035830234");

        assertThat(target.getYear(), is(equalTo(1958)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(13)));
    }

    @Test
    public void extractDateFromFnr2000Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("21050556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr1900Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("61039596234");

        assertThat(target.getYear(), is(equalTo(1995)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr2000Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("51050586534");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(11)));
    }

    @Test
    public void extractDateFromBost1900Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("21250546234");

        assertThat(target.getYear(), is(equalTo(1905)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromBost2000Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("21250556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromFnr1800Century() {
        LocalDate target = DatoUtil.hentAlderFraFnr("21256556234");

        assertThat(target.getYear(), is(equalTo(1865)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }
}