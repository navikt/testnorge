package no.nav.registre.bisys.service.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static no.nav.registre.bisys.service.utils.DateUtilsV2.getBirthdate;
import static no.nav.registre.bisys.service.utils.DateUtilsV2.getMonthsBetween;
import static no.nav.registre.bisys.service.utils.DateUtilsV2.getAgeInMonths;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    private static final String BIRTHDAY_1 = "01810150000";

    @Test
    public void shouldGetCorrectBirthdays(){
        var birthday2 = "01870100000";
        var birthday3 = "01920150000";

        assertThat(getBirthdate(BIRTHDAY_1)).isEqualTo(LocalDate.of(2001,1,1));
        assertThat(getBirthdate(birthday2)).isEqualTo(LocalDate.of(1901,7,1));
        assertThat(getBirthdate(birthday3)).isEqualTo(LocalDate.of(2001,12,1));
    }

    @Test
    public void shouldGetNumberOfMonthsBetweenDates(){
        var firstDate = LocalDate.of(2020, 1,1);
        var secondDate = LocalDate.of(2021, 1, 1);

        assertThat(getMonthsBetween(firstDate, secondDate)).isEqualTo(12);
        assertThat(getMonthsBetween(firstDate, firstDate)).isEqualTo(0);
        assertThat(getMonthsBetween(firstDate.minusYears(2).plusDays(2), secondDate)).isEqualTo(35);
    }

    @Test
    public void shouldGetAgeInMonths(){
        assertThat(getAgeInMonths(BIRTHDAY_1, LocalDate.of(2004, 1, 1))).isEqualTo(36);
        assertThat(getAgeInMonths(BIRTHDAY_1, LocalDate.of(2001, 1, 20))).isEqualTo(0);
        assertThat(getAgeInMonths(BIRTHDAY_1, LocalDate.of(2001, 7, 20))).isEqualTo(6);
        assertThat(getAgeInMonths(BIRTHDAY_1, LocalDate.of(2000, 11, 20))).isEqualTo(-2);
    }

}
