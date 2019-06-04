package no.nav.registre.core.provider.rs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.core.database.model.Person;
import no.nav.registre.core.service.PersonService;

@Slf4j
@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping("/")
    public ResponseEntity<List<Person>> opprettPerson(@RequestBody List<Person> person) {
        return ResponseEntity.ok(person.parallelStream().map(personService::opprettPerson).collect(Collectors.toList()));
    }

    @GetMapping("/{fnr}")
    public ResponseEntity<Person> finnPerson(@PathVariable String fnr) {
        Person person = personService.finnPerson(fnr);
        if (person == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(person);
    }

}
