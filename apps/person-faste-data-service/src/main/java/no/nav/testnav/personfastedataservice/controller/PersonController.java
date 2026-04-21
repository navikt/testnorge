package no.nav.testnav.personfastedataservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;
import no.nav.testnav.personfastedataservice.config.AllowedHosts;
import no.nav.testnav.personfastedataservice.domain.Person;
import no.nav.testnav.personfastedataservice.service.PersonService;
import org.springframework.http.ResponseEntity;
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
            HttpServletRequest httpServletRequest
    ) {
        var person = personService.save(new Person(personDTO), gruppe);
        var requestUrl = httpServletRequest.getRequestURL().toString();

        if (!allowedHosts.getHosts().contains(httpServletRequest.getServerName())) {
            return ResponseEntity
                    .status(403)
                    .body("Host " + httpServletRequest.getServerName() + " er ikke tilatt. Tilatt hosts er " + allowedHosts.getHosts() + ".");
        }

        var uri = new URI(requestUrl + "/" + person.getIdent());
        return ResponseEntity
                .created(uri)
                .body(person.toDTO());
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> get(
            @RequestParam(required = false) Gruppe gruppe,
            @RequestParam(required = false) String opprinnelse,
            @RequestParam(required = false) String tag
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
