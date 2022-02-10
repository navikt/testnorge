package no.nav.registre.testnorge.personsearchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.testnorge.personsearchservice.domain.Person;
import no.nav.registre.testnorge.personsearchservice.domain.PersonList;
import no.nav.registre.testnorge.personsearchservice.controller.search.PersonSearch;
import no.nav.registre.testnorge.personsearchservice.service.PersonSearchService;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;


@RestController
@RequestMapping("/api/v1/person")
@RequiredArgsConstructor
public class PersonSearchController {
    private static final String NUMBER_OF_ITEMS_HEADER = "NUMBER_OF_ITEMS";
    private final PersonSearchService service;

    @PostMapping
    public ResponseEntity<List<PersonDTO>> search(@RequestBody PersonSearch search) {
        PersonList personList = service.search(search);
        List<PersonDTO> dto = personList.getList().stream().map(Person::toDTO).toList();
        return ResponseEntity
                .ok()
                .header(NUMBER_OF_ITEMS_HEADER, String.valueOf(personList.getNumberOfItems()))
                .body(dto);
    }

}
