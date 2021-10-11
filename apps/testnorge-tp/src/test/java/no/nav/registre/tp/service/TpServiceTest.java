package no.nav.registre.tp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tp.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.tp.consumer.rs.TpSyntConsumer;
import no.nav.registre.tp.database.models.HistorikkComposityKey;
import no.nav.registre.tp.database.models.TForhold;
import no.nav.registre.tp.database.models.TForholdYtelseHistorikk;
import no.nav.registre.tp.database.models.TPerson;
import no.nav.registre.tp.database.models.TYtelse;
import no.nav.registre.tp.database.repository.TForholdRepository;
import no.nav.registre.tp.database.repository.TForholdYtelseHistorikkRepository;
import no.nav.registre.tp.database.repository.TPersonRepository;
import no.nav.registre.tp.database.repository.TYtelseRepository;
import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
public class TpServiceTest {

    private static final Integer MIN_AGE = 13;

    @Mock
    private TForholdYtelseHistorikkRepository tForholdYtelseHistorikkRepository;

    @Mock
    private TForholdRepository tForholdRepository;

    @Mock
    private TPersonRepository tPersonRepository;

    @Mock
    private TYtelseRepository tYtelseRepository;

    @Mock
    private TpSyntConsumer tpSyntConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private TpService tpService;

    private SyntetiseringsRequest request = new SyntetiseringsRequest(1L, "q2", 3);
    private TYtelse ytelse1;

    @Before
    public void setUp() {
        var fnrs = new ArrayList<>(Arrays.asList("123", "132", "321"));
        ytelse1 = new TYtelse();
        ytelse1.setYtelseId(3);
        ytelse1.setErGyldig("0");
        ytelse1.setFunkYtelseId("10637556");
        ytelse1.setKYtelseT("UKJENT");
        ytelse1.setEndretAv("");
        ytelse1.setVersjon("2");

        var ytelser = new ArrayList<>(
                Arrays.asList(
                        ytelse1,
                        new TYtelse(),
                        new TYtelse()
                ));

        var expectedForhold = new TForhold();
        expectedForhold.setPersonId(1);
        expectedForhold.setForholdId(2);
        expectedForhold.setEndretAv("");

        var person = new TPerson();
        person.setPersonId(1);
        person.setFnrFk("123");

        when(hodejegerenConsumer.getLevende(anyLong())).thenReturn(fnrs);
        when(hodejegerenConsumer.getLevende(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(fnrs);
        when(tPersonRepository.save(any())).thenReturn(person);
        when(tForholdRepository.save(any())).thenReturn(expectedForhold);
        when(tpSyntConsumer.getSyntYtelser(3)).thenReturn(ytelser);
        when(tYtelseRepository.save(any())).thenReturn(ytelse1);
        when(tForholdYtelseHistorikkRepository.save(any())).thenReturn(new TForholdYtelseHistorikk(new HistorikkComposityKey(2, 3)));

        var forhold = new TForhold();
        forhold.setPersonId(1);
        forhold.setForholdId(2);
        forhold.setEndretAv("");

        var forholdListe = new ArrayList<>(Collections.singletonList(forhold));
        when(tForholdRepository.findAll()).thenReturn(forholdListe);

        when(hodejegerenHistorikkConsumer.saveHistory(any())).thenReturn(fnrs);
    }

    /**
     * Scenario: Alt fungerer slik det skal
     */
    @Test
    public void syntetiser() {
        tpService.syntetiser(request);

        verify(tForholdRepository, times(3)).save(any());
    }

    /**
     * Scenario: Feil antall ytelser i forhold til fnr
     */
    @Test
    public void syntetiserForMangeForhold() {
        var ytelser = new ArrayList<>(Arrays.asList(
                ytelse1,
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse()
        ));

        when(tpSyntConsumer.getSyntYtelser(3)).thenReturn(ytelser);
        tpService.syntetiser(request);

        verify(tForholdRepository, times(3)).save(any());
    }

    /**
     * Scenario: Ikke nok ytelser ble syntetisert
     */
    @Test(expected = HttpServerErrorException.class)
    public void syntetiserForMangeFNR() {
        var fnrs = new ArrayList<>(Arrays.asList(
                "123",
                "132",
                "321",
                "312",
                "213",
                "231"
        ));
        when(hodejegerenConsumer.getLevende(anyLong(), anyString(), anyInt(), eq(MIN_AGE))).thenReturn(fnrs);

        tpService.syntetiser(request);
        verify(tForholdRepository, times(0)).save(any());
    }

    @Test
    public void getForhold() {
        var forhold = tpService.getForhold();
        var tForhold = forhold.get(0);
        assertEquals(1, (int) tForhold.getPersonId());
        assertEquals(2, (int) tForhold.getForholdId());
    }

    @Test
    public void initializeTpDbForEnvironemnt() {
        tpService.initializeTpDbForEnvironment(1L);
        verify(tPersonRepository).saveAll(any());
    }

    @Test
    public void createPeopleAllCreated() {
        var fnrs = new ArrayList<>(Arrays.asList(
                "123",
                "132",
                "321",
                "312",
                "213",
                "231"
        ));

        var people = fnrs.parallelStream().map(fnr -> {
            var person = new TPerson();
            person.setFnrFk(fnr);
            return person;
        }).collect(Collectors.toList());

        for (var s : fnrs) {
            when(tPersonRepository.findByFnrFk(s)).thenReturn(null);
        }

        when(tPersonRepository.saveAll(any())).thenReturn(people);
        var created = tpService.createPeople(fnrs);

        for (var s : fnrs) {
            assertTrue(created.contains(s));
        }
    }

    @Test
    public void createPeopleTwoExists() {
        ArrayList<String> fnrs = new ArrayList<>(Arrays.asList(
                "123",
                "132",
                "321",
                "312",
                "213",
                "231"
        ));

        var person1 = new TPerson();
        person1.setFnrFk("123");
        var person2 = new TPerson();
        person2.setFnrFk("132");

        when(tPersonRepository.findByFnrFk("123")).thenReturn(person1);
        when(tPersonRepository.findByFnrFk("132")).thenReturn(person2);
        for (int i = 2; i < fnrs.size(); i++) {
            when(tPersonRepository.findByFnrFk(fnrs.get(i))).thenReturn(null);
        }

        var people = fnrs
                .parallelStream()
                .filter(
                fnr -> !fnr.equals("123") && !fnr.equals("132"))
                .map(fnr -> {
                    var person = new TPerson();
                    person.setFnrFk(fnr);
                    return person;
                })
                .collect(Collectors.toList());
        when(tPersonRepository.saveAll(any())).thenReturn(people);
        var created = tpService.createPeople(fnrs);

        for (var s : fnrs) {
            if (s.equals("123") || s.equals("132")) {
                continue;
            }
            assertTrue(created.contains(s));
        }
    }
}