package no.nav.registre.tp.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.tp.consumer.HodejegerenConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import no.nav.registre.tp.database.models.TForhold;
import no.nav.registre.tp.database.models.TPerson;
import no.nav.registre.tp.database.repository.TForholdRepository;
import no.nav.registre.tp.database.repository.TPersonRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class TpServiceTest {

    @Mock
    private TForholdRepository tForholdRepository;

    @Mock
    private TPersonRepository tPersonRepository;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @InjectMocks
    private TpService tpService;


    @Before
    public void setUp() {
        var fnrs = new ArrayList<>(Arrays.asList("123", "132", "321"));

        var expectedForhold = new TForhold();
        expectedForhold.setPersonId(1);
        expectedForhold.setForholdId(2);
        expectedForhold.setEndretAv("");

        var person = new TPerson();
        person.setPersonId(1);
        person.setFnrFk("123");

        when(hodejegerenConsumer.getLevende(anyLong())).thenReturn(fnrs);
        when(tPersonRepository.save(any())).thenReturn(person);
        when(tForholdRepository.save(any())).thenReturn(expectedForhold);

        var forhold = new TForhold();
        forhold.setPersonId(1);
        forhold.setForholdId(2);
        forhold.setEndretAv("");

        var forholdListe = new ArrayList<>(Collections.singletonList(forhold));
        when(tForholdRepository.findAll()).thenReturn(forholdListe);

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