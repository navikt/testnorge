package no.nav.registre.arena.core.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class IdentUtilsTest {

    @Test
    public void extractDateFromFnr1900Century() {
        LocalDate foedselsdato = IdentUtils.hentFoedseldato("13035830234");

        assertThat(foedselsdato.getYear(), is(equalTo(1958)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(3)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(13)));
    }

    @Test
    public void extractDateFromFnr2000Century() {

        LocalDate foedselsdato = IdentUtils.hentFoedseldato("21050556234");

        assertThat(foedselsdato.getYear(), is(equalTo(2005)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(5)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr1900Century() {

        LocalDate foedselsdato = IdentUtils.hentFoedseldato("61039596234");

        assertThat(foedselsdato.getYear(), is(equalTo(1995)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(3)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromDnr2000Century() {

        LocalDate foedselsdato = IdentUtils.hentFoedseldato("51050586534");

        assertThat(foedselsdato.getYear(), is(equalTo(2005)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(5)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(11)));
    }

    @Test
    public void extractDateFromBost1900Century() {

        LocalDate foedselsdato = IdentUtils.hentFoedseldato("21250546234");

        assertThat(foedselsdato.getYear(), is(equalTo(1905)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(5)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromBost2000Century() {

        LocalDate foedselsdato = IdentUtils.hentFoedseldato("21250556234");

        assertThat(foedselsdato.getYear(), is(equalTo(2005)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(5)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(21)));
    }

    @Test
    public void extractDateFromFnr1800Century() {
        LocalDate foedselsdato = IdentUtils.hentFoedseldato("21256556234");

        assertThat(foedselsdato.getYear(), is(equalTo(1865)));
        assertThat(foedselsdato.getMonthValue(), is(equalTo(5)));
        assertThat(foedselsdato.getDayOfMonth(), is(equalTo(21)));
    }
}
