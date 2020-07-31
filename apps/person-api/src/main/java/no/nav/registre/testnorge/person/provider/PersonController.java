package no.nav.registre.testnorge.person.provider;

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
import no.nav.registre.testnorge.person.service.PersonService;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @PostMapping
    public ResponseEntity<Object> createPerson(
            @RequestHeader
            @RequestBody PersonDTO personDTO
    ) {
        service.createPerson(new Person(personDTO));

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ident}")
                .buildAndExpand(personDTO.getIdent())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ident}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {
        Person person = service.getPerson((ident));

        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person.toDTO());
    }
}
