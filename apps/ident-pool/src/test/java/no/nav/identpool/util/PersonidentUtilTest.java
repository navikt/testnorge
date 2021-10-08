package no.nav.identpool.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class PersonidentUtilTest {

    @Test
    void extractDateFromFnr1900Century() {
        LocalDate target = PersonidentUtil.toBirthdate("13035830234");

        assertThat(target.getYear(), is(equalTo(1958)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(13)));
    }

    @Test
    void extractDateFromFnr2000Century() {

        LocalDate target = PersonidentUtil.toBirthdate("21050556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    void extractDateFromDnr1900Century() {

        LocalDate target = PersonidentUtil.toBirthdate("61039596234");

        assertThat(target.getYear(), is(equalTo(1995)));
        assertThat(target.getMonthValue(), is(equalTo(3)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    void extractDateFromDnr2000Century() {

        LocalDate target = PersonidentUtil.toBirthdate("51050586534");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(11)));
    }

    @Test
    void extractDateFromBost1900Century() {

        LocalDate target = PersonidentUtil.toBirthdate("21250546234");

        assertThat(target.getYear(), is(equalTo(1905)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    void extractDateFromBost2000Century() {

        LocalDate target = PersonidentUtil.toBirthdate("21250556234");

        assertThat(target.getYear(), is(equalTo(2005)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    void extractDateFromFnr1800Century() {
        LocalDate target = PersonidentUtil.toBirthdate("21256556234");

        assertThat(target.getYear(), is(equalTo(1865)));
        assertThat(target.getMonthValue(), is(equalTo(5)));
        assertThat(target.getDayOfMonth(), is(equalTo(21)));
    }
}