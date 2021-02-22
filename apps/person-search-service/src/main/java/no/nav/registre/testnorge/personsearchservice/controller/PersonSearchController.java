package no.nav.registre.testnorge.personsearchservice.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.personsearchservice.controller.dto.PersonDTO;
import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.registre.testnorge.personsearchservice.domain.Search;
import no.nav.registre.testnorge.personsearchservice.service.PersonSearchService;

@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonSearchController {

    private final PersonSearchService service;

    @PostMapping
    public ResponseEntity<List<PersonDTO>> search(@RequestBody Search search) {
        PersonList personList = service.search(search);
        return ResponseEntity.ok(personList.getList().stream().map(Person::toDTO).collect(Collectors.toList()));
    }

}
