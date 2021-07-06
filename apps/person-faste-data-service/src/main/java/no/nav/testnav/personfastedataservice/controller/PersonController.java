package no.nav.testnav.personfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.personfastedataservice.controller.dto.PersonDTO;
import no.nav.testnav.personfastedataservice.domain.Gruppe;
import no.nav.testnav.personfastedataservice.domain.Person;
import no.nav.testnav.personfastedataservice.service.PersonService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personer")
public class PersonController {
    private final PersonService personService;

    @SneakyThrows
    @PutMapping
    public ResponseEntity<PersonDTO> save(
            @RequestBody PersonDTO personDTO,
            @RequestParam Gruppe gruppe,
            ServerHttpRequest serverHttpRequest
    ) {
        var person = personService.save(new Person(personDTO), gruppe);
        var uri = new URI(serverHttpRequest.getURI() + "/" + person.getIdent());
        return ResponseEntity.created(uri).body(person.toDTO());
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> get(
            @RequestParam(required = false) Gruppe gruppe,
            @RequestParam(required = false) String opprinnelse,
            @RequestParam(required = false) String tag,
            ServerHttpRequest serverHttpRequest
    ) {
        log.info(serverHttpRequest.getURI().toString());
        var personer = personService.getBy(gruppe, opprinnelse, tag);
        return ResponseEntity.ok(personer.stream().map(Person::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{ident}")
    public ResponseEntity<PersonDTO> get(
            @PathVariable String ident
    ) {
        var person = personService.get(ident);
        return person
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }
}
