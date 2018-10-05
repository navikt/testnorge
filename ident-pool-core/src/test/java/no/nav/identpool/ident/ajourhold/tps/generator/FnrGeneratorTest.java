package no.nav.identpool.ident.ajourhold.tps.generator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Ordering;

import org.junit.Test;

import no.nav.identpool.ident.ajourhold.util.PersonIdentifikatorUtil;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;

public class FnrGeneratorTest {

    @Test
    public void fnrGenererDescendingTest() {
        // This test will stop working 1. Jan 2040 :(
        LocalDate localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap = FnrGenerator.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR);
        assertEquals(1, pinMap.size());
        assertTrue(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)));
    }

    @Test
    public void fnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjonn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.getKjonn(fnr), Kjoenn.MANN));
        menn.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        menn.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        menn.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) < 4));

        List<String> kvinner = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjonn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.getKjonn(fnr), Kjoenn.KVINNE));
        kvinner.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        kvinner.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        kvinner.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) < 4));

        List<String> people = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .antall(size)
                        .foedtEtter(localDate)
                        .build());
        people.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        people.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        people.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) < 4));

        List<String> none = FnrGenerator.genererIdenter(PersonKriterier.builder().foedtEtter(localDate).build());
        assertEquals(none.size(), 0);
    }

    @Test
    public void dnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjonn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.getKjonn(fnr), Kjoenn.MANN));
        menn.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        menn.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        menn.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));

        List<String> kvinner = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjonn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.getKjonn(fnr), Kjoenn.KVINNE));
        kvinner.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        kvinner.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        kvinner.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));

        List<String> people = FnrGenerator.genererIdenter(
                PersonKriterier.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .build());
        people.forEach(fnr -> assertTrue(PersonIdentifikatorUtil.gyldigPersonidentifikator(fnr)));
        people.forEach(fnr -> assertEquals(PersonIdentifikatorUtil.toBirthdate(fnr), localDate));
        people.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));
    }
}
