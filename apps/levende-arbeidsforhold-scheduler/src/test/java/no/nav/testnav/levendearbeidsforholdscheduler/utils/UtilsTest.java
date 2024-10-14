package no.nav.testnav.levendearbeidsforholdscheduler.utils;

import org.junit.jupiter.api.Test;

import static no.nav.testnav.levendearbeidsforholdscheduler.utils.Utils.beregnEkstraDelay;

class UtilsTest {

    @Test
    public void testBeregnEkstraDelay(){
        assert beregnEkstraDelay(2, 3, 3, 5) == 345600000;
        assert beregnEkstraDelay(2, 3, 6, 5) == 334800000;
        assert beregnEkstraDelay(5, 10, 23, 3) == 126000000;
    }
}