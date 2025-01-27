package no.nav.testnav.dollysearchservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import no.nav.testnav.dollysearchservice.domain.Person;
import no.nav.testnav.dollysearchservice.service.PersonSearchService;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PersonSearchController {

    private static final String NUMBER_OF_ITEMS_HEADER = "NUMBER_OF_ITEMS";

    private final PersonSearchService personSearchService;

    @PostMapping("/person")
    public ResponseEntity<List<PersonDTO>> search(@RequestBody PersonSearch search) {

        return null;
//
//        var personer = personSearchService.search(search)
//                .map(Person::toDTO)
//                .collectList()
//                .block();
//
//        log.info("Fant {} personer i pdl.", nonNull(personer) ? personer.size() : 0);
//
//        return ResponseEntity
//                .ok()
//                .header(NUMBER_OF_ITEMS_HEADER, String.valueOf(nonNull(personer) ? personer.size() : 0))
//                .body(personer);
    }
}
