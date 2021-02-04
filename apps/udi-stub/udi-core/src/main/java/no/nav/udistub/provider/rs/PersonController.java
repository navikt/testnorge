package no.nav.udistub.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.exception.CouldNotCreatePersonException;
import no.nav.udistub.exception.CouldNotUpdatePersonException;
import no.nav.udistub.exception.NotFoundException;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonControllerResponse> opprettPerson(@RequestBody UdiPerson udiPerson) {
        UdiPerson createdPerson = personService.opprettPerson(udiPerson)
                .orElseThrow(() -> new CouldNotCreatePersonException(String.format("Kunne ikke opprette person med fnr:%s", udiPerson.getIdent())));
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonControllerResponse(createdPerson));
    }

    @PutMapping
    public ResponseEntity<PersonControllerResponse> oppdaterPerson(@RequestBody UdiPerson udiPerson) {
        UdiPerson updatedPerson = personService.oppdaterPerson(udiPerson)
                .orElseThrow(() -> new CouldNotUpdatePersonException(String.format("Kunne ikke oppdatere person med fnr:%s", udiPerson.getIdent())));
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonControllerResponse(updatedPerson));

    }

    @GetMapping("/{ident}")
    public ResponseEntity<PersonControllerResponse> finnPerson(@PathVariable String ident) {
        UdiPerson foundPerson = personService.finnPerson(ident)
                .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s", ident)));
        return ResponseEntity.status(HttpStatus.OK).body(new PersonControllerResponse(foundPerson));
    }

    @DeleteMapping
    public ResponseEntity<PersonControllerResponse> deletePerson(@RequestHeader(name = "Nav-Personident") String ident) {
        personService.deletePerson(ident);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
