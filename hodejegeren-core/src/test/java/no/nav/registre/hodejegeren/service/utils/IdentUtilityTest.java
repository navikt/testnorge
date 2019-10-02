package no.nav.registre.hodejegeren.service.utils;

import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class IdentUtilityTest {

    @Test
    public void extractDateFromFnr1900Century() {
        LocalDate target = getFoedselsdatoFraFnr("13035830234");

        assertThat(target.getYear(), is(equalTo(1958)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(13)));
    }

    @Test
    public void extractDateFromFnr2000Century() {
        LocalDate target = getFoedselsdatoFraFnr("21050556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr1900Century() {
        LocalDate target = getFoedselsdatoFraFnr("61039596234");

        assertThat(target.getYear(), is(equalTo(1995)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr2000Century() {
        LocalDate target = getFoedselsdatoFraFnr("51050586534");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(11)));
    }

    @Test
    public void extractDateFromBost1900Century() {
        LocalDate target = getFoedselsdatoFraFnr("21250546234");

        assertThat(target.getYear(), is(equalTo(1905)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromBost2000Century() {
        LocalDate target = getFoedselsdatoFraFnr("21250556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromFnr1800Century() {
        LocalDate target = getFoedselsdatoFraFnr("21256556234");

        assertThat(target.getYear(), is(equalTo(1865)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }
}
