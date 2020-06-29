package no.nav.registre.testnorge.person.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody PersonDTO personDTO) {
        service.createPerson(new Person(personDTO));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Person getPerson(@RequestParam String ident) {
        //Validering på at ident består av 11 tegn?
        Person person = service.getPerson((ident));
        return ResponseEntity.ok(person).getBody();
        //return ResponseEntity.ok(person.toDTO()).getBody();
    }
}
