package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.domain.entity.DbPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import no.nav.pdl.forvalter.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

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
}
