package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import no.nav.pdl.forvalter.dto.RsPerson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;
    private final MapperFacade mapperFacade;
    private final IdentPoolConsumer identPoolConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;

    @Transactional
    public RsPerson updatePerson(String ident, PersonUpdateRequest request) {

        var dbPerson = personRepository.findByIdent(ident)
                .orElseGet(() -> personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PdlPerson.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()));

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setSistOppdatert(now());

        return mapperFacade.map(personRepository.save(dbPerson), RsPerson.class);
    }

    @Transactional
    public void deletePerson(String ident) {

        var dbPerson = personRepository.findByIdent(ident).orElseThrow(() ->
                new HttpClientErrorException(NOT_FOUND, format("Ident %s ble ikke funnet", ident)));

        var personer = Stream.of(List.of(dbPerson),
                dbPerson.getRelasjoner().stream()
                        .map(DbRelasjon::getRelatertPerson)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var identer = personer.stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toList());

        pdlTestdataConsumer.delete(identer);
        identPoolConsumer.releaseIdents(identer);
        personRepository.deleteByIdentIn(identer);
    }

    @Transactional(readOnly = true)
    public RsPerson getPerson(String ident) {

        return mapperFacade.map(getDbPerson(ident), RsPerson.class);
    }

    private DbPerson getDbPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, format("Ident %s ble ikke funnet", ident)));
    }
}
