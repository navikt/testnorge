package no.nav.testnav.personfastedataservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.testnav.personfastedataservice.domain.Gruppe;
import no.nav.testnav.personfastedataservice.domain.Person;
import no.nav.testnav.personfastedataservice.repository.PersonRepository;
import no.nav.testnav.personfastedataservice.repository.model.PersonEntity;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository repository;

    public Person save(Person person, Gruppe gruppe) {
        return repository
                .save(person.toEntity(gruppe))
                .getPerson();
    }

    public Optional<Person> get(String ident) {
        return repository
                .findById(ident)
                .map(PersonEntity::getPerson);
    }


    public void delete(String ident) {
        repository.deleteById(ident);
    }

    public List<Person> getBy(Gruppe gruppe, String opprinnelse, String tag) {
        var stream = gruppe != null
                ? repository.findAllByGruppe(gruppe).stream()
                : StreamSupport.stream(repository.findAll().spliterator(), false);
        return stream
                .map(PersonEntity::getPerson)
                .filter(person -> opprinnelse == null || opprinnelse.equals(person.getOpprinnelse()))
                .filter(person -> tag == null || person.getTags() != null && person.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}