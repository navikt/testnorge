package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.PersonConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.domain.person.Person;
import no.nav.registre.sdforvalter.domain.person.PersonStatus;
import no.nav.registre.sdforvalter.domain.person.PersonStatusMap;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonConsumer personConsumer;
    private final TpsIdenterAdapter tpsIdenterAdapter;

    public PersonStatusMap getStatusMap(String gruppe, Boolean equal) {
        TpsIdentListe tpsIdentListe = tpsIdenterAdapter.fetchBy(gruppe);
        Set<String> identer = tpsIdentListe.stream().map(TpsIdent::getFnr).collect(Collectors.toSet());
        Map<String, Person> personMap = personConsumer
                .hentPersoner(identer)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Person::getFnr, person -> person));

        return new PersonStatusMap(
                tpsIdentListe
                        .stream()
                        .map(ident -> new PersonStatus(new Person(ident), personMap.get(ident.getFnr())))
                        .filter(personStatus -> equal == null || personStatus.isEqual() == equal)
                        .collect(Collectors.toList())
        );
    }

    public List<Person> getPerson(String gruppe) {
        TpsIdentListe tpsIdentListe = tpsIdenterAdapter.fetchBy(gruppe);
        return tpsIdentListe.stream().map(Person::new).collect(Collectors.toList());
    }

    public TpsIdent getPersonByIdent(String ident) {
        return tpsIdenterAdapter.fetchByIdent(ident);
    }
}
