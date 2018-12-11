package no.nav.dolly.util;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UtilFunctionsTest {

    @Test
    public void isNullOrEmpty() {
        Object o = null;
        assertThat(UtilFunctions.isNullOrEmpty(o), is(true));
    }

    @Test
    public void isNullOrEmptyCollection() {
        Collection<Object> colNotEmpty = new ArrayList<>();
        colNotEmpty.add(new Object());

        Collection<Object> colEmpty = new ArrayList<>();
        Collection<Object> colNull = null;

        assertThat(UtilFunctions.isNullOrEmpty(colEmpty), is(true));
        assertThat(UtilFunctions.isNullOrEmpty(colNull), is(true));
        assertThat(UtilFunctions.isNullOrEmpty(colNotEmpty), is(false));
    }

    @Test
    public void isNullOrEmptyString() {
        String notEmpty = "Hei";
        String sEmpty = "";
        String sNull = null;

        assertThat(UtilFunctions.isNullOrEmpty(sEmpty), is(true));
        assertThat(UtilFunctions.isNullOrEmpty(sNull), is(true));
        assertThat(UtilFunctions.isNullOrEmpty(notEmpty), is(false));
    }
}