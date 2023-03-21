package no.nav.testnav.apps.personservice.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.apps.personservice.service.PdlSyncService;
import no.nav.testnav.apps.personservice.service.PersonService;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.lang.String.format;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PdlSyncService pdlSyncService;

    @PostMapping
    public ResponseEntity<Object> createPerson(
            @RequestBody PersonDTO personDTO,
            @RequestHeader(required = false) String kilde
    ) {
        var person = new Person(personDTO);
        var pdlKilde = StringUtils.isBlank(kilde) ? "DOLLY" : kilde;
        var ident = personService.ordrePerson(person, pdlKilde);

        var uri = UriComponentsBuilder
                .fromPath("/api/v1/personer/{ident}")
                .buildAndExpand(ident)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ident}")
    public Mono<PersonDTO> getPerson(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {

        return personService
                .getPerson(ident)
                .map(value -> value
                        .map(Person::toDTO)
                        .orElseThrow(() -> new NotFoundException(format("Ident %s ikke funnet", ident)))
                );
    }

    @GetMapping("/{ident}/exists")
    public Mono<Boolean> isPerson(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {

        return personService.isPerson(ident);
    }

    @GetMapping("/{ident}/sync")
    public Boolean syncPdlPersonReady(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {

        return pdlSyncService.syncPdlPersonReady(ident);
    }

    @GetMapping("/{ident}/aktoerId")
    public Mono<Optional<AktoerIdent>> getAktoerId(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident
    ) {

        return personService.getAktoerId(ident);
    }
}
