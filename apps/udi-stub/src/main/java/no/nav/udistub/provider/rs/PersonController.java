package no.nav.udistub.provider.rs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.service.PersonService;
import no.nav.udistub.service.dto.UdiPerson;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonControllerResponse opprettPerson(@RequestBody UdiPerson udiPerson) throws JacksonException {
        log.info("Mottatt request opprettPerson: {}", objectMapper.writeValueAsString(udiPerson));
        return PersonControllerResponse.builder()
                .person(personService.opprettPerson(udiPerson))
                .build();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonControllerResponse oppdaterPerson(@RequestBody UdiPerson udiPerson) throws JacksonException {
        log.info("Mottatt request oppdaterPerson: {}", objectMapper.writeValueAsString(udiPerson));
        return PersonControllerResponse.builder()
                .person(personService.oppdaterPerson(udiPerson))
                .build();
    }

    @GetMapping("/{ident}")
    @ResponseStatus(HttpStatus.OK)
    public PersonControllerResponse finnPerson(@PathVariable String ident) {
        log.info("Mottatt request finnPerson med ident: {}", ident);
        return PersonControllerResponse.builder()
                .person(personService.finnPerson(ident))
                .build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deletePerson(@RequestHeader(name = "Nav-Personident") String ident) {
        log.info("Mottatt request deletePerson med ident: {}", ident);
        personService.deletePerson(ident);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonControllerResponse {

        private UdiPerson person;
        private Map<String, Object> reason;
    }
}
