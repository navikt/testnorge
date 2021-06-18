package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.FullPersonDTO;
import no.nav.pdl.forvalter.domain.PersonDTO;
import no.nav.pdl.forvalter.domain.PersonUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke endres. Benytt gjeldende ident %s for denne operasjonen";

    private final PersonRepository personRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;
    private final MapperFacade mapperFacade;
    private final IdentPoolConsumer identPoolConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final AliasRepository aliasRepository;

    @Transactional
    public String updatePerson(String ident, PersonUpdateRequestDTO request) {

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident)
                .orElseGet(() -> personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PersonDTO.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()));

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setSistOppdatert(now());

        return personRepository.save(dbPerson).getIdent();
    }

    @Transactional
    public void deletePerson(String ident) {

        checkAlias(ident);
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
    public FullPersonDTO getPerson(String ident) {

        var aliasPerson = aliasRepository.findByTidligereIdent(ident);
        if (aliasPerson.isPresent()) {
            return mapperFacade.map(aliasPerson.get().getPerson(), FullPersonDTO.class);
        } else {
            return mapperFacade.map(getDbPerson(ident), FullPersonDTO.class);
        }
    }

    private DbPerson getDbPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, format("Ident %s ble ikke funnet", ident)));
    }

    private void checkAlias(String ident) {

        var alias = aliasRepository.findByTidligereIdent(ident);
        if (alias.isPresent()) {
            throw new HttpClientErrorException(BAD_REQUEST,
                    format(VIOLATION_ALIAS_EXISTS, alias.get().getPerson().getIdent()));
        }
    }
}
