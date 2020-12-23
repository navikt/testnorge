package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.arbeidsforhold.adapter.OppsummeringsdokumentetRawAdapter;

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter/raw/items")
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawController {
    private static final String NUMBER_OF_ELEMENTS = "Number-Of-Elements";
    private final OppsummeringsdokumentetRawAdapter oppsummeringsdokumentetRawAdapter;

    @GetMapping(value = "/{position}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo,
            @PathVariable("position") Integer position
    ) {
        var list = oppsummeringsdokumentetRawAdapter.fetchAllByPosition(miljo, position);

        if (list.getDocuments().isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .header(NUMBER_OF_ELEMENTS, list.getNumberOfPages().toString())
                    .build();
        }

        return ResponseEntity
                .ok()
                .header(NUMBER_OF_ELEMENTS, list.getNumberOfPages().toString())
                .body(list.getDocuments().get(0));
    }
}