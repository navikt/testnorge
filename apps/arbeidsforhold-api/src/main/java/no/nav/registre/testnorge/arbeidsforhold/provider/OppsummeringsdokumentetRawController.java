package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

import no.nav.registre.testnorge.arbeidsforhold.adapter.OppsummeringsdokumentetRawAdapter;
import no.nav.registre.testnorge.arbeidsforhold.domain.Document;

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter/raw/items")
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawController {
    private static final String NUMBER_OF_ELEMENTS = "Number-Of-Elements";
    private final OppsummeringsdokumentetRawAdapter oppsummeringsdokumentetRawAdapter;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo,
            @RequestParam("page") Integer page,
            @RequestParam(value = "fom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam(value = "tom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom
    ) {
        var list = oppsummeringsdokumentetRawAdapter.fetchBy(miljo, page, fom, tom);

        if (list.getDocuments().isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .header(NUMBER_OF_ELEMENTS, list.getNumberOfPages().toString())
                    .build();
        }

        var document = list.getDocuments().get(0);

        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(document.getId())
                .toUriString();

        return ResponseEntity
                .ok()
                .header(NUMBER_OF_ELEMENTS, list.getNumberOfPages().toString())
                .header(HttpHeaders.LOCATION, uri)
                .body(document.getXml());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getItem(@PathVariable("id") Long id) {
        var document = oppsummeringsdokumentetRawAdapter.findById(id);

        if (document == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(document);
    }
}