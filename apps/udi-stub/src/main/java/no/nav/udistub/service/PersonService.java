package no.nav.udistub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.udistub.database.model.Person;
import no.nav.udistub.database.repository.PersonRepository;
import no.nav.udistub.exception.NotFoundException;
import no.nav.udistub.service.dto.UdiPerson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final MapperFacade mapperFacade;
    private final PersonRepository personRepository;

    @Transactional
    public UdiPerson opprettPerson(UdiPerson udiPerson) {

        Person nyPerson = personRepository.save(Person.builder()
                .ident(udiPerson.getIdent())
                .build());

        mapperFacade.map(udiPerson, nyPerson);
        return mapperFacade.map(nyPerson, UdiPerson.class);
    }

    @Transactional
    public UdiPerson oppdaterPerson(UdiPerson udiPerson) {

        var endrePerson = personRepository.findByIdent(udiPerson.getIdent())
                .orElseThrow(() -> new NotFoundException(String.format("Person med ident %s ikke funnet i database", udiPerson.getIdent())));

        mapperFacade.map(udiPerson, endrePerson);
        return mapperFacade.map(endrePerson, UdiPerson.class);
    }

    @Transactional
    public UdiPerson finnPerson(String ident) {

        return personRepository.findByIdent(ident)
                .map(person -> mapperFacade.map(person, UdiPerson.class))
                .orElseThrow(() -> new NotFoundException(String.format("Person med ident %s ikke funnet i database", ident)));
    }

    @Transactional
    public void deletePerson(String ident) {

        var slettePerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format("Person med ident %s ikke funnet i database", ident)));
        personRepository.deleteById(slettePerson.getId());
    }
}
