package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
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
    private final MapperFacade mapperFacade;

    @Transactional
    public DbPerson updatePerson(String ident, PersonUpdateRequest request) {

        var dbPerson = personRepository.findByIdent(ident)
                .orElse(DbPerson.builder()
                        .ident(ident)
                        .person(new PdlPerson())
                        .build());

        dbPerson.setPerson(new MergeService(request.getPdlPerson(), dbPerson.getPerson(), mapperFacade).call());
        dbPerson.setUpdated(LocalDateTime.now());

        return personRepository.save(dbPerson);
    }

    @Transactional
    public void deletePerson(String ident) {

        personRepository.deleteByIdent(ident);
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
