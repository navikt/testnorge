package no.nav.registre.tss.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RunWith(MockitoJUnitRunner.class)
public class PersonTest {

    private String navn = "navn";

    @Test
    public void extractDateFromFnr1900Century() {
        Person p = new Person("13035830234", navn);
        LocalDate birthdate = LocalDate.of(1958, 3, 13);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromFnr2000Century() {
        Person p = new Person("21050556234", navn);
        LocalDate birthdate = LocalDate.of(2005, 5, 21);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromDnr1900Century() {
        Person p = new Person("61039596234", navn);
        LocalDate birthdate = LocalDate.of(1995, 3, 21);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromDnr2000Century() {
        Person p = new Person("51050586534", navn);
        LocalDate birthdate = LocalDate.of(2005, 5, 11);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromBost1900Century() {
        Person p = new Person("21250546234", navn);
        LocalDate birthdate = LocalDate.of(1905, 5, 21);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromBost2000Century() {
        Person p = new Person("20250556234", navn);
        LocalDate birthdate = LocalDate.of(2005, 5, 20);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }

    @Test
    public void extractDateFromFnr1800Century() {
        Person p = new Person("21256556234", navn);
        LocalDate birthdate = LocalDate.of(1865, 5, 21);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(p.getAlder(), is(equalTo(alder)));
    }
}
