package no.nav.registre.sam.utils;

import static no.nav.registre.sam.utils.DateUtils.formatDate;
import static no.nav.registre.sam.utils.DateUtils.formatTimestamp;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    @Test
    public void shouldFormatStringToTimestamp() throws ParseException {
        assertThat(formatTimestamp("25.02.2009").toString(), startsWith("2009-02-25"));
    }

    @Test
    public void shouldFormatStringToDate() throws ParseException {
        assertThat(formatDate("25.02.2009").toString(), startsWith("2009-02-25"));
    }
}