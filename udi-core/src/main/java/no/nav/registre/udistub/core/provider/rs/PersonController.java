package no.nav.registre.udistub.core.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.udistub.core.exception.CouldNotCreatePersonException;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.PersonService;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    @PostMapping
    public ResponseEntity<PersonControllerResponse> opprettPerson(@Valid @RequestBody UdiPerson udiPerson,
            @RequestHeader(NAV_CONSUMER_ID) String consumerId) {
        UdiPerson createdPerson = personService.opprettPerson(udiPerson, consumerId)
                .orElseThrow(() -> new CouldNotCreatePersonException(String.format("Kunne ikke opprette person med fnr:%s", udiPerson.getIdent())));
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonControllerResponse(createdPerson));
    }

    @GetMapping
    public ResponseEntity<PersonControllerResponse> finnPerson(@RequestHeader(name = "Nav-Personident") String ident) {
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
