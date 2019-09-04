package no.nav.registre.udistub.core.service;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.udistub.core.database.model.Person;
import no.nav.registre.udistub.core.database.repository.PersonRepository;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.to.UdiAlias;
import no.nav.registre.udistub.core.service.to.UdiAvgjorelse;
import no.nav.registre.udistub.core.service.to.UdiPersonNavn;
import no.nav.registre.udistub.core.service.to.UdiPerson;
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

    public Optional<UdiPerson> opprettPerson(UdiPerson udiPerson, String consumerId) {

        fetchAndSetTpsfPersonData(udiPerson, consumerId);

        Person person = mapperFacade.map(udiPerson, Person.class);
        Person storedPerson = personRepository.save(person);

        UdiPerson storedAndMappedUdiPerson = mapStoredPerson(storedPerson);
        return Optional.of(storedAndMappedUdiPerson);
    }

    public Optional<UdiPerson> finnPerson(String ident) {
        Person storedPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException("Kunne ikke finne person med ident " + ident));

        UdiPerson storedAndMappedUdiPerson = mapStoredPerson(storedPerson);
        return Optional.of(storedAndMappedUdiPerson);
    }

    public void deletePerson(String ident) {
        Optional<Person> optionalPerson = personRepository.findByIdent(ident);
        if (optionalPerson.isPresent()) {
            personRepository.deleteById(optionalPerson.get().getId());
        } else {
            throw new NotFoundException(format("Kunne ikke slette person med ident:%s, da personen ikke ble funnet", ident));
        }
    }

    public void fetchAndSetTpsfPersonData(UdiPerson udiPerson, String consumerId) {
        TpsfPerson tpsfPerson = tpsfService.hentPersonWithIdent(udiPerson.getIdent(), consumerId);

        UdiPersonNavn udiPersonNavn = new UdiPersonNavn(tpsfPerson.getFornavn(), tpsfPerson.getMellomnavn(), tpsfPerson.getEtternavn());
        udiPerson.setNavn(udiPersonNavn);
        udiPerson.setIdent(tpsfPerson.getIdent());

        udiPerson.setAliaser(Collections.singletonList(
                UdiAlias.builder()
                        .fnr(tpsfPerson.getIdent())
                        .navn(udiPersonNavn)
                        .person(udiPerson)
                        .build()));

        udiPerson.setFoedselsDato(tpsfPerson.getFoedselsdato());
        udiPerson.getOppholdStatus().setPerson(udiPerson);

        udiPerson.setAvgjoerelser(Collections.singletonList(
                UdiAvgjorelse.builder()
                        .person(udiPerson)
                        .build()));

        udiPerson.getArbeidsadgang().setPerson(udiPerson);
    }

    private UdiPerson mapStoredPerson(Person storedPerson) {
        // This is a workaround to resolve the Orikamappers stack overflow error
        // encountered when mapping cycling objects
        storedPerson.getAvgjoerelser().forEach(avgjorelse -> avgjorelse.setPerson(null));
        storedPerson.getAliaser().forEach(alias -> alias.setPerson(null));
        storedPerson.getOppholdStatus().setPerson(null);
        storedPerson.getArbeidsadgang().setPerson(null);
        UdiPerson mappedPerson = mapperFacade.map(storedPerson, UdiPerson.class);
        mappedPerson.getAvgjoerelser().forEach(avgjorelseTo -> avgjorelseTo.setPerson(mappedPerson));
        mappedPerson.getAliaser().forEach(udiAlias -> udiAlias.setPerson(mappedPerson));
        mappedPerson.getOppholdStatus().setPerson(mappedPerson);
        mappedPerson.getArbeidsadgang().setPerson(mappedPerson);
        return mappedPerson;
    }
}
