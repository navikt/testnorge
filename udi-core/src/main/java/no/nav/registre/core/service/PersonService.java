package no.nav.registre.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.core.exception.NotFoundException;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.core.database.model.Alias;
import no.nav.registre.core.database.model.Arbeidsadgang;
import no.nav.registre.core.database.model.Avgjoerelse;
import no.nav.registre.core.database.model.opphold.OppholdStatus;
import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.database.model.PersonNavn;
import no.nav.registre.core.database.repository.AliasRepository;
import no.nav.registre.core.database.repository.ArbeidsAdgangRepository;
import no.nav.registre.core.database.repository.AvgjoerelseRepository;
import no.nav.registre.core.database.repository.OppholdStatusRepository;
import no.nav.registre.core.database.repository.PersonRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final AliasRepository aliasRepository;
    private final ArbeidsAdgangRepository arbeidsAdgangRepository;
    private final AvgjoerelseRepository avgjoerelseRepository;
    private final OppholdStatusRepository oppholdStatusRepository;
    private final PersonRepository personRepository;

    public Person finnPerson(String fnr) {
        return personRepository.findByFnr(fnr).orElseThrow(() -> new NotFoundException("Could not find " + fnr));
    }

    public List<Person> opprettPersoner(List<Person> personer) {
        return personer.parallelStream().map(this::opprettPerson).collect(Collectors.toList());
    }

    public List<Alias> opprettAliaserPaaFnr(String fnr, List<Alias> aliaser) {
        return aliaser.parallelStream().map(a -> opprettAlias(fnr, a.getNavn())).collect(Collectors.toList());
    }

    public List<Avgjoerelse> opprettAvgjoerelserPaaFnr(String fnr, List<Avgjoerelse> avgjoerelser) {
        return avgjoerelser.parallelStream().map(a -> opprettAvgjoerelse(fnr, a))
                .collect(Collectors.toList());
    }

    public Person opprettPerson(Person person) {
        if (person.getOppholdStatus() != null)
            person.getOppholdStatus().setPerson(person);
        if (person.getArbeidsadgang() != null)
            person.getArbeidsadgang().setPerson(person);
        return personRepository.save(person);
    }

    public Arbeidsadgang opprettArbeidsAdgang(String fnr, Arbeidsadgang arbeidsadgang) {
        return personRepository.findByFnr(fnr).map(person -> {
            arbeidsadgang.setPerson(person);
            return arbeidsAdgangRepository.save(arbeidsadgang);
        }).orElse(null);
    }

    private Alias opprettAlias(String fnr, PersonNavn navn) {
        return personRepository.findByFnr(fnr).map(person ->
                Alias.builder()
                        .fnr(fnr)
                        .navn(navn)
                        .person(person)
                        .build()
        ).orElse(null);
    }

    private Avgjoerelse opprettAvgjoerelse(String fnr, Avgjoerelse avgjoerelse) {
        return personRepository.findByFnr(fnr).map(person -> {
            avgjoerelse.setPerson(person);
            Avgjoerelse lagretAvgjoerelse = avgjoerelseRepository.save(avgjoerelse);
            lagretAvgjoerelse.setOmgjortAvgjoerelsesId(lagretAvgjoerelse.getId().toString());
            return avgjoerelseRepository.save(lagretAvgjoerelse);
        }).orElse(null);
    }

    public OppholdStatus opprettOppholdsStatus(String fnr, OppholdStatus oppholdsStatus) {
        return personRepository.findByFnr(fnr).map(person -> {
            oppholdsStatus.setPerson(person);
            return oppholdStatusRepository.save(oppholdsStatus);
        }).orElse(null);
    }

    public List<Avgjoerelse> findAvgjoerelserByFnr(String fnr) {
        return personRepository.findByFnr(fnr).map(Person::getAvgjoerelser).orElse(null);
    }

    public List<Alias> findAliasByFnr(String fnr) {
        return personRepository.findByFnr(fnr).map(Person::getAliaser).orElse(null);
    }

    public Arbeidsadgang findArbeidsAdgangByFnr(String fnr) {
        return personRepository.findByFnr(fnr).map(Person::getArbeidsadgang).orElse(null);
    }

    private JaNeiUavklart convertFraBool(Boolean avklart) {
        if (avklart == null) {
            return JaNeiUavklart.UAVKLART;
        }
        return avklart ? JaNeiUavklart.JA : JaNeiUavklart.NEI;
    }
}
