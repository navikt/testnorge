package no.nav.testnav.apps.personservice.provider.v1;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.apps.personservice.service.PersonService;
import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public Mono<ResponseEntity<Object>> createPerson(
            @RequestBody PersonDTO personDTO,
            @RequestHeader(required = false) String kilde
    ) {
        var person = new Person(personDTO);
        var pdlKilde = StringUtils.isBlank(kilde) ? "DOLLY" : kilde;
        return personService.ordrePerson(person, pdlKilde)
                .map(ident -> {
                    var uri = UriComponentsBuilder
                            .fromPath("/api/v1/personer/{ident}")
                            .buildAndExpand(ident)
                            .toUri();
                    return ResponseEntity.created(uri).build();
                });
    }

    @GetMapping("/{ident}")
    public Mono<PersonDTO> getPerson(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident) {

        return personService
                .getPerson(ident)
                .map(value -> value
                        .map(Person::toDTO)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)))
                );
    }

    @GetMapping("/{ident}/exists")
    public Mono<Boolean> isPerson(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident,
            @RequestParam(value = "opplysningId", required = false) Set<String> opplysningId) {

        return personService.isPerson(ident, opplysningId);
    }

    @GetMapping("/{ident}/aktoerId")
    public Mono<Optional<AktoerIdent>> getAktoerId(
            @PathVariable("ident") @Size(min = 11, max = 11, message = "Ident må ha 11 siffer") String ident
    ) {

        return personService.getAktoerId(ident);
    }
}
