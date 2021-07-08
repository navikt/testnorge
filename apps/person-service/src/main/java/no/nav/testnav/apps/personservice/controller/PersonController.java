package no.nav.testnav.apps.personservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Size;

import no.nav.testnav.apps.personservice.service.PersonService;
import no.nav.testnav.dto.personservice.v1.Persondatasystem;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService service;

    @GetMapping("/{ident}")
    public Mono<ResponseEntity<?>> getPerson(
            @RequestHeader Persondatasystem persondatasystem,
            @RequestHeader(required = false) String miljoe,
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident
    ) {
        if (persondatasystem == Persondatasystem.TPS && miljoe == null) {
            return Mono.just(ResponseEntity.badRequest().body("Kunne ikke hente person fra TPS. Miljø ikke satt"));
        }

        return service
                .getPerson(ident, miljoe, persondatasystem)
                .map(value -> value
                        .map(person -> ResponseEntity.ok(person.toDTO()))
                        .orElse(ResponseEntity.notFound().build())
                );
    }
}
