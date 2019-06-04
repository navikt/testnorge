package no.nav.registre.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.core.database.model.Alias;
import no.nav.registre.core.database.model.ArbeidsAdgang;
import no.nav.registre.core.database.model.Avgjoerelse;
import no.nav.registre.core.database.model.OppholdsStatus;
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
        Optional<Person> person = personRepository.findById(fnr);
        return person.orElse(null);
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
        if (person.getOppholdsStatus() != null)
            person.getOppholdsStatus().setPerson(person);
        if (person.getArbeidsAdgang() != null)
            person.getArbeidsAdgang().setPerson(person);
        return personRepository.save(person);
    }

    public ArbeidsAdgang opprettArbeidsAdgang(String fnr, ArbeidsAdgang arbeidsAdgang) {
        Optional<Person> person = personRepository.findById(fnr);
        if (person.isPresent()) {
            arbeidsAdgang.setPerson(person.get());
            return arbeidsAdgangRepository.save(arbeidsAdgang);
        }
        return null;
    }

    public Alias opprettAlias(String fnr, PersonNavn navn) {
        Optional<Person> person = personRepository.findById(fnr);
        if (person.isPresent()) {
            Alias alias = Alias.builder()
                    .fnr(fnr)
                    .navn(navn)
                    .person(person.get())
                    .build();
            return aliasRepository.save(alias);
        }
        return null;
    }

    public Avgjoerelse opprettAvgjoerelse(String fnr, Avgjoerelse avgjoerelse) {
        Optional<Person> person = personRepository.findById(fnr);
        if (person.isPresent()) {
            avgjoerelse.setPerson(person.get());
            Avgjoerelse lagretAvgjoerelse = avgjoerelseRepository.save(avgjoerelse);
            lagretAvgjoerelse.setOmgjortAvgjoerelsesId(lagretAvgjoerelse.getId().toString());
            return avgjoerelseRepository.save(lagretAvgjoerelse);
        }
        return null;
    }

    public OppholdsStatus opprettOppholdsStatus(String fnr, OppholdsStatus oppholdsStatus) {
        Optional<Person> person = personRepository.findById(fnr);
        if (person.isPresent()) {
            oppholdsStatus.setPerson(person.get());
            return oppholdStatusRepository.save(oppholdsStatus);
        }
        return null;
    }

    public List<Avgjoerelse> findAvgjoerelserByFnr(String fnr) {
        Optional<Person> optionalPerson = personRepository.findById(fnr);
        return optionalPerson.map(Person::getAvgjoerelser).orElse(null);
    }

    public List<Alias> findAliasByFnr(String fnr) {
        Optional<Person> optionalPerson = personRepository.findById(fnr);
        return optionalPerson.map(Person::getAliaser).orElse(null);
    }

    public ArbeidsAdgang findArbeidsAdgangByFnr(String fnr) {
        Optional<Person> optionalPerson = personRepository.findById(fnr);
        return optionalPerson.map(Person::getArbeidsAdgang).orElse(null);
    }

    private JaNeiUavklart convertFraBool(Boolean avklart) {
        if (avklart == null) {
            return JaNeiUavklart.UAVKLART;
        }
        return avklart ? JaNeiUavklart.JA : JaNeiUavklart.NEI;
    }
}
