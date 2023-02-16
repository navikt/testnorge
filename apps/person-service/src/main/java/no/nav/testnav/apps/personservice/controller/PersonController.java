package no.nav.testnav.apps.personservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.service.PdlSyncService;
import no.nav.testnav.apps.personservice.service.PersonService;
import no.nav.testnav.libs.dto.personservice.v1.Persondatasystem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import javax.validation.constraints.Size;

import no.nav.testnav.apps.personservice.domain.Person;

import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;

import org.springframework.web.util.UriComponentsBuilder;
import java.util.Optional;

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
        Person person = new Person(personDTO);
        var pdlKilde = kilde == null ? "DOLLY" : kilde;
        String ident = personService.createPerson(person, pdlKilde);

        var uri = UriComponentsBuilder
                .fromPath("/api/v1/personer/{ident}")
                .buildAndExpand(ident)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ident}")
    public Mono<ResponseEntity<?>> getPerson(
            @RequestHeader Persondatasystem persondatasystem,
            @RequestHeader(required = false) String miljoe,
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {

        if (persondatasystem == Persondatasystem.TPS && miljoe == null) {
            return Mono.just(ResponseEntity.badRequest().body("Kunne ikke hente person fra TPS. Miljø ikke satt"));
        }

        return personService
                .getPerson(ident, miljoe, persondatasystem)
                .map(value -> value
                        .map(person -> ResponseEntity.ok(person.toDTO()))
                        .orElse(ResponseEntity.notFound().build())
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
