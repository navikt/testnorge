package no.nav.testnav.personfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.v1.PersonDTO;
import no.nav.testnav.personfastedataservice.config.AllowedHosts;
import no.nav.testnav.personfastedataservice.domain.Gruppe;
import no.nav.testnav.personfastedataservice.domain.Person;
import no.nav.testnav.personfastedataservice.service.PersonService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personer")
public class PersonController {
    private final PersonService personService;
    private final AllowedHosts allowedHosts;

    @SneakyThrows
    @PutMapping
    public ResponseEntity<?> save(
            @RequestBody PersonDTO personDTO,
            @RequestHeader Gruppe gruppe,
            ServerHttpRequest serverHttpRequest
    ) {
        var person = personService.save(new Person(personDTO), gruppe);
        var requestUri = serverHttpRequest.getURI();

        if (!allowedHosts.getHosts().contains(requestUri.getHost())) {
            return ResponseEntity
                    .status(403)
                    .body("Host " + requestUri.getHost() + " er ikke tilatt. Tilatt hosts er " + allowedHosts.getHosts() + ".");
        }

        var uri = new URI(requestUri + "/" + person.getIdent());
        return ResponseEntity
                .created(uri)
                .body(person.toDTO());
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> get(
            @RequestParam(required = false) Gruppe gruppe,
            @RequestParam(required = false) String opprinnelse,
            @RequestParam(required = false) String tag,
            ServerHttpRequest serverHttpRequest
    ) {
        var personer = personService.getBy(gruppe, opprinnelse, tag);
        return ResponseEntity.ok(personer.stream().map(Person::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{ident}")
    public ResponseEntity<PersonDTO> get(@PathVariable String ident) {
        var person = personService.get(ident);
        return person
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ident}")
    public ResponseEntity<?> delete(@PathVariable String ident) {
        personService.delete(ident);
        return ResponseEntity.noContent().build();
    }
}
