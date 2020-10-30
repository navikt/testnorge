package no.nav.registre.testnorge.mn.personservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.mn.personservice.domain.Person;
import no.nav.registre.testnorge.mn.personservice.service.PersonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/person")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getPerson() {
        List<PersonDTO> list = personService.getMiniNorgePersoner()
                .stream()
                .map(Person::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}

