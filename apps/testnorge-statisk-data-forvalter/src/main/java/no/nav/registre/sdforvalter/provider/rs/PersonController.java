package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.domain.person.Person;
import no.nav.registre.sdforvalter.domain.person.PersonStatusMap;
import no.nav.registre.sdforvalter.service.PersonService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/person")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> getPersonByGruppe(
            @RequestParam(value = "gruppe") Gruppe gruppe
    ) {
        return ResponseEntity.ok(personService.getPerson(gruppe.name()));
    }

    @GetMapping("/pdl/status")
    public ResponseEntity<PersonStatusMap> statusByGruppe(
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe") Gruppe gruppe
    ) {
        return ResponseEntity.ok(personService.getStatusMap(gruppe.name(), equal));
    }

    @Operation(summary = "Hent person fra Team Dollys database (faste data)")
    @GetMapping("/{ident}")
    public ResponseEntity<PersonDTO> getPersonByIdent(@PathVariable String ident) {
        var person = personService.getPersonByIdent(ident);
        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person.toDTO());
    }
}