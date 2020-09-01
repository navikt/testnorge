package no.nav.registre.testnorge.person.provider;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Size;
import java.net.URI;

import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.domain.Persondatasystem;
import no.nav.registre.testnorge.person.service.PersonService;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @PostMapping
    @ApiOperation(value = "Oppretter person", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<Object> createPerson(
            @RequestBody PersonDTO personDTO
    ) {
        Person person = new Person(personDTO);
        String ident = service.createPerson(person);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ident}")
                .buildAndExpand(ident)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ident}")
    @ApiOperation(value = "Henter person", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> getPerson(
            @RequestHeader Persondatasystem persondatasystem,
            @RequestHeader(required = false) String miljoe,
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident
    ) {
        if (persondatasystem == Persondatasystem.TPS && miljoe == null) {
            return ResponseEntity.badRequest().body("Kunne ikke hente person fra TPS. Miljø ikke satt");
        }
        Person person = service.getPerson(ident, miljoe, persondatasystem);

        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person.toDTO());
    }
}
