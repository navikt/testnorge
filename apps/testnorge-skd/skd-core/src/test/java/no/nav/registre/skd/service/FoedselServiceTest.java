package no.nav.registre.skd.service;

import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
public class FoedselServiceTest {

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private FoedselService foedselService;

    /**
     * Testscenario: HVIS det skal opprettes fødselsmelding, skal systemet i metoden {@link FoedselService#findMoedre}, hente en
     * eksisterende ident som kan være mor til barnet. Identen skal være kvinne, hvilket betyr at det tredje nummeret i
     * individsifferet skal være et partall.
     */
    @Test
    public void shouldFindEksisterendeIdent() {
        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010111111");
        levendeIdenterINorge.add("01010101010");
        levendeIdenterINorge.add("02020202020");
        levendeIdenterINorge.add("03030303030");

        when(rand.nextInt(anyInt())).thenReturn(0);

        var potensielleMoedre = foedselService.findMoedre(1, levendeIdenterINorge, "0");

        assertEquals(levendeIdenterINorge.get(1), potensielleMoedre.get(0));
    }

    /**
     * Testscenario: Systemet skal opprette relasjonen fra barn til mor i metoden {@link FoedselService#behandleFoedselsmeldinger},
     * påse at mor er eldre enn barn, og opprette barn til mor-relasjonen.
     */
    @Test
    public void shouldFindChildForMother() {
        List<RsMeldingstype> meldinger = new ArrayList<>(Collections.singletonList(new RsMeldingstype1Felter()));
        var levendeIdenterINorge = new ArrayList<>(Collections.singletonList("01010101010"));
        var barnFnr = "10101054321";

        when(identPoolConsumer.hentNyeIdenter(any())).thenReturn(Collections.singletonList(barnFnr));

        var opprettedeBarn = foedselService.behandleFoedselsmeldinger(FNR, meldinger, levendeIdenterINorge);

        assertTrue(opprettedeBarn.contains(barnFnr));
        assertEquals(barnFnr, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
        assertEquals(levendeIdenterINorge.get(0), ((RsMeldingstype1Felter) meldinger.get(0)).getMorsFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getMorsPersonnummer());
    }

    /**
     * Testscenario: Systemet skal finne en far til barnet basert på mors alder, barns alder og tidligere valgte mødre
     */
    @Test
    public void shouldFindFatherForChild() {
        var morFnr = "22060156889";
        var levendeIdenterINorge = new ArrayList<>(Arrays.asList("22060156889", "22050167791", "30111757809", "26101767990"));
        var moedre = new ArrayList<>(Arrays.asList("22060156889", "30111657809"));
        var barnFnr = "21051767891";
        var farFnr = foedselService.findFar(morFnr, barnFnr, levendeIdenterINorge, moedre);

        assertThat(farFnr, equalTo("22050167791"));
    }

    /**
     * Scenario: HVIS identpool returnerer en exception, skal systemet logge feilen og fjerne den gjeldende skdmeldingen
     */
    @Test
    public void shouldLogWarningAndRemoveMessage() {
        List<RsMeldingstype> meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());
        meldinger.add(new RsMeldingstype1Felter());
        meldinger.add(new RsMeldingstype1Felter());

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010101010");
        levendeIdenterINorge.add("02020202020");
        levendeIdenterINorge.add("03030303030");

        Logger logger = (Logger) LoggerFactory.getLogger(FoedselService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        when(identPoolConsumer.hentNyeIdenter(any())).thenThrow(HttpClientErrorException.class);

        var opprettedeBarn = foedselService.behandleFoedselsmeldinger(FNR, meldinger, levendeIdenterINorge);

        assertEquals(0, opprettedeBarn.size());
        assertEquals(0, meldinger.size());
        assertEquals(3, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Kunne ikke finne barn til mor med fnr " + levendeIdenterINorge.get(0)));
    }
}
