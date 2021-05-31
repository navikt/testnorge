package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;

    @Transactional
    public DbPerson updatePerson(String ident, PersonUpdateRequest request) {

        var dbPerson = personRepository.findByIdent(ident)
                .orElse(DbPerson.builder()
                        .ident(ident)
                        .person(new PdlPerson())
                        .build());

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson, ident);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setSistOppdatert(LocalDateTime.now());

        return personRepository.save(dbPerson);
    }

    @Transactional
    public void deletePerson(String ident) {

        if (personRepository.deleteByIdent(ident) == 0) {
            throw new HttpClientErrorException(NOT_FOUND, format("Ident %s ble ikke funnet", ident));
        }
    }

    @Transactional
    public PdlPerson getPerson(String ident) {

        return getDbPerson(ident).getPerson();
    }

    private DbPerson getDbPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, format("Ident %s ble ikke funnet", ident)));
    }
}
