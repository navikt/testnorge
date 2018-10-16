package no.nav.identpool.ident.ajourhold.tps.generator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.google.common.collect.Ordering;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;
import no.nav.identpool.util.PersonidentifikatorUtil;

public class
FnrGeneratorTest {

    @Test
    public void fnrGenererDescendingTest() {
        // This test will stop working 1. Jan 2040 :(
        LocalDate localDate = LocalDate.now();
        Map<LocalDate, List<String>> pinMap = IdentGenerator.genererIdenterMap(localDate, localDate.plusDays(1), Identtype.FNR);
        assertEquals(1, pinMap.size());
        assertTrue(Ordering.natural().reverse().isOrdered(pinMap.get(localDate)));
    }

    @Test
    public void fnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .antall(size)
                        .identtype(Identtype.FNR)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(fnr -> assertEquals(PersonidentifikatorUtil.getKjonn(fnr), Kjoenn.MANN));
        menn.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        menn.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        menn.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) < 4));

        List<String> kvinner = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .antall(size)
                        .identtype(Identtype.FNR)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(fnr -> assertEquals(PersonidentifikatorUtil.getKjonn(fnr), Kjoenn.KVINNE));
        kvinner.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        kvinner.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        kvinner.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) < 4));

        List<String> people = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .build());
        people.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        people.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        people.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));
    }

    @Test
    public void dnrGenererKjonnKriterier() {
        LocalDate localDate = LocalDate.now();
        int size = 100;
        List<String> menn = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.MANN)
                        .build());

        assertEquals(menn.size(), size);
        menn.forEach(fnr -> assertEquals(PersonidentifikatorUtil.getKjonn(fnr), Kjoenn.MANN));
        menn.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        menn.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        menn.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));

        List<String> kvinner = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .kjoenn(Kjoenn.KVINNE)
                        .build());

        assertEquals(kvinner.size(), size);
        kvinner.forEach(fnr -> assertEquals(PersonidentifikatorUtil.getKjonn(fnr), Kjoenn.KVINNE));
        kvinner.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        kvinner.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        kvinner.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));

        List<String> people = IdentGenerator.genererIdenter(
                HentIdenterRequest.builder()
                        .identtype(Identtype.DNR)
                        .antall(size)
                        .foedtEtter(localDate)
                        .build());
        people.forEach(fnr -> assertTrue(PersonidentifikatorUtil.gyldigPersonidentifikator(fnr)));
        people.forEach(fnr -> assertEquals(PersonidentifikatorUtil.toBirthdate(fnr), localDate));
        people.forEach(fnr -> assertTrue(Character.getNumericValue(fnr.charAt(0)) > 3));
    }
}
