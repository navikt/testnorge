package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.domain.person.Person;
import no.nav.registre.sdforvalter.domain.person.PersonStatusMap;
import no.nav.registre.sdforvalter.service.PersonService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/person")
@Tag(
        name = "PersonController",
        description = "Henter personer fra database."
)
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @Operation(
            description = "Hent liste over personer fra tabell TPS_IDENTER basert på angitt gruppe."
    )
    public List<Person> getPersonByGruppe(
            @RequestParam(value = "gruppe") Gruppe gruppe
    ) {
        return personService.getPerson(gruppe.name());
    }

    @GetMapping("/pdl/status")
    @Operation(
            description = "Hent liste over status per fødselsnummer basert på angitt gruppe. Personer hentes fra tabell TPS_IDENTER, status på disse fra tjeneste testnav-person-service."
    )
    public PersonStatusMap statusByGruppe(
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe") Gruppe gruppe
    ) {
        return personService.getStatusMap(gruppe.name(), equal);
    }

    @Operation(summary = "Hent person fra Team Dollys database (faste data)")
    @GetMapping("/{ident}")
    public PersonDTO getPersonByIdent(@PathVariable String ident) {
        var person = personService.getPersonByIdent(ident);
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke person med ident " + ident);
        }
        return person.toDTO();
    }
}