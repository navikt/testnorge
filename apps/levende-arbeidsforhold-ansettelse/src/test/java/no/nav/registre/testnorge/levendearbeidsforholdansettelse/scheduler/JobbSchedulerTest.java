package no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.scheduler.JobbScheduler.gyldigTidsrom;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JobbSchedulerTest {

    @Test
    public void testGyldigTidsrom(){

        //Test for dag - Skal bli true
        assert gyldigTidsrom(12, 6, 16, 2, 13, 7);

        //Test for dag - Skal bli false
        assert !gyldigTidsrom(12, 6, 16, 2, 13, 3);

        //Test for klokkeslett startdag - Skal bli true
        assert gyldigTidsrom(12, 6, 16, 2, 13, 6);

        //Test for klokkeslett sluttdag - Skal bli true
        assert gyldigTidsrom(12, 6, 16, 2, 13, 2);

        //Test for klokkeslett sluttdag - Skal bli false
        assert !gyldigTidsrom(12, 6, 16, 2, 16, 2);

        //Test for klokkeslett startdag - Skal bli false
        assert !gyldigTidsrom(12, 6, 16, 2, 11, 6);

        //Test for dag - Skal bli false
        assert !gyldigTidsrom(12, 3, 16, 1, 13, 2);

        //Test for dag - Skal bli false
        assert !gyldigTidsrom(12, 5, 16, 7, 13, 4);

        //Test for dag - Skal bli false
        assert !gyldigTidsrom(12, 4, 16, 4, 13, 5);

        //Test for dag - Skal bli true
        assert gyldigTidsrom(12, 5, 16, 5, 13, 5);
    }
}