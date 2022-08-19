package no.nav.testnav.apps.tpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import no.nav.testnav.apps.tpservice.consumer.rs.HodejegerenConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.apps.tpservice.database.models.TPerson;
import no.nav.testnav.apps.tpservice.database.repository.TForholdRepository;
import no.nav.testnav.apps.tpservice.database.repository.TPersonRepository;

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

    private static final List<String> FNRS = new ArrayList<>(Arrays.asList(
            "123",
            "132",
            "321",
            "312",
            "213",
            "231"
    ));

    @Test
    public void initializeTpDbForEnvironemnt() {
        when(hodejegerenConsumer.getLevende(anyLong())).thenReturn(FNRS);
        tpService.initializeTpDbForEnvironment(1L);
        verify(tPersonRepository).saveAll(any());
    }

    @Test
    public void createPeopleAllCreated() {
        var people = FNRS.parallelStream()
                .map(fnr -> {
                    var person = new TPerson();
                    person.setFnrFk(fnr);
                    return person;
                }).toList();

        for (var s : FNRS) {
            when(tPersonRepository.findByFnrFk(s)).thenReturn(null);
        }

        when(tPersonRepository.saveAll(any())).thenReturn(people);
        var created = tpService.createPeople(FNRS);

        for (var s : FNRS) {
            assertThat(created).contains(s);
        }
    }

    @Test
    public void createPeopleTwoExists() {
        var person1 = new TPerson();
        person1.setFnrFk("123");
        var person2 = new TPerson();
        person2.setFnrFk("132");

        when(tPersonRepository.findByFnrFk("123")).thenReturn(person1);
        when(tPersonRepository.findByFnrFk("132")).thenReturn(person2);
        for (int i = 2; i < FNRS.size(); i++) {
            when(tPersonRepository.findByFnrFk(FNRS.get(i))).thenReturn(null);
        }

        var people = FNRS
                .parallelStream()
                .filter(fnr -> !fnr.equals("123") && !fnr.equals("132"))
                .map(fnr -> {
                    var person = new TPerson();
                    person.setFnrFk(fnr);
                    return person;
                })
                .collect(Collectors.toList());

        when(tPersonRepository.saveAll(any())).thenReturn(people);

        var created = tpService.createPeople(FNRS);

        for (var s : FNRS) {
            if (s.equals("123") || s.equals("132")) {
                assertThat(created).doesNotContain(s);
            } else {
                assertThat(created).contains(s);
            }
        }
    }
}