package no.nav.registre.udistub.core.service;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.udistub.core.database.model.Person;
import no.nav.registre.udistub.core.database.repository.PersonRepository;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.to.AliasTo;
import no.nav.registre.udistub.core.service.to.AvgjorelseTo;
import no.nav.registre.udistub.core.service.to.PersonNavnTo;
import no.nav.registre.udistub.core.service.to.PersonTo;
import no.nav.registre.udistub.core.service.tpsf.TpsfPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    @Autowired
    private TpsfService tpsfService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private final PersonRepository personRepository;

    public Optional<PersonTo> opprettPerson(PersonTo personTo, String consumerId) {

        fetchAndSetTpsfPersonData(personTo, consumerId);

        Person person = mapperFacade.map(personTo, Person.class);
        Person storedPerson = personRepository.save(person);

        PersonTo storedAndMappedPersonTo = mapStoredPerson(storedPerson);
        return Optional.of(storedAndMappedPersonTo);
    }

    public Optional<PersonTo> finnPerson(String ident) {
        Person storedPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException("Kunne ikke finne person med ident " + ident));

        PersonTo storedAndMappedPersonTo = mapStoredPerson(storedPerson);
        return Optional.of(storedAndMappedPersonTo);
    }

    public void deletePerson(String ident) {
        Optional<Person> optionalPerson = personRepository.findByIdent(ident);
        if (optionalPerson.isPresent()) {
            personRepository.deleteById(optionalPerson.get().getId());
        } else {
            throw new NotFoundException(format("Kunne ikke slette person med ident:%s, da personen ikke ble funnet", ident));
        }
    }

    public void fetchAndSetTpsfPersonData(PersonTo personTo, String consumerId) {
        TpsfPerson tpsfPerson = tpsfService.hentPersonWithIdent(personTo.getIdent(), consumerId);

        PersonNavnTo personNavnTo = new PersonNavnTo(tpsfPerson.getFornavn(), tpsfPerson.getMellomnavn(), tpsfPerson.getEtternavn());
        personTo.setNavn(personNavnTo);
        personTo.setIdent(tpsfPerson.getIdent());

        personTo.setAliaser(Collections.singletonList(
                AliasTo.builder()
                        .fnr(tpsfPerson.getIdent())
                        .navn(personNavnTo)
                        .person(personTo)
                        .build()));

        personTo.setFoedselsDato(tpsfPerson.getFoedselsdato());
        personTo.getOppholdStatus().setPerson(personTo);

        personTo.setAvgjoerelser(Collections.singletonList(
                AvgjorelseTo.builder()
                        .person(personTo)
                        .build()));

        personTo.getArbeidsadgang().setPerson(personTo);
    }

    private PersonTo mapStoredPerson(Person storedPerson) {
        // This is a workaround to resolve the Orikamappers stack overflow error
        // encountered when mapping cycling objects
        storedPerson.getAvgjoerelser().forEach(avgjorelse -> avgjorelse.setPerson(null));
        storedPerson.getAliaser().forEach(alias -> alias.setPerson(null));
        storedPerson.getOppholdStatus().setPerson(null);
        storedPerson.getArbeidsadgang().setPerson(null);
        PersonTo mappedPerson = mapperFacade.map(storedPerson, PersonTo.class);
        mappedPerson.getAvgjoerelser().forEach(avgjorelseTo -> avgjorelseTo.setPerson(mappedPerson));
        mappedPerson.getAliaser().forEach(aliasTo -> aliasTo.setPerson(mappedPerson));
        mappedPerson.getOppholdStatus().setPerson(mappedPerson);
        mappedPerson.getArbeidsadgang().setPerson(mappedPerson);
        return mappedPerson;
    }
}
