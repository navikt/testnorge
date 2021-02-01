package no.nav.registre.testnorge.oppsummeringsdokuemntservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

import no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter.OppsummeringsdokumentAdapter;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain.Oppsummeringsdokument;

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter/raw/items")
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawController {
    private static final String NUMBER_OF_ELEMENTS_HEADER = "Number-Of-Elements";
    private static final String ELEMENT_ID_HEADER = "Element-Id";
    private final OppsummeringsdokumentAdapter adapter;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo,
            @RequestParam("page") Integer page,
            @RequestParam(value = "ident",required = false) String ident,
            @RequestParam(value = "typeArbeidsforhold", required = false) String typeArbeidsforhold,
            @RequestParam(value = "fom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam(value = "tom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom
    ) {
        var pages = adapter.getAllCurrentDocumentsBy(miljo, fom, tom, ident, typeArbeidsforhold, page);
        var document = pages.stream().findFirst();

        if(document.isEmpty()){
            return ResponseEntity
                    .noContent()
                    .header(NUMBER_OF_ELEMENTS_HEADER, "0")
                    .build();
        }

        var oppsummeringsdokument = document.get();
        return ResponseEntity
                .ok()
                .header(NUMBER_OF_ELEMENTS_HEADER, String.valueOf(pages.getTotalElements()))
                .header(ELEMENT_ID_HEADER, oppsummeringsdokument.getId())
                .body(oppsummeringsdokument.toXml());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getItem(@PathVariable("id") String id) {
        var document = adapter.get(id);

        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(document.toXml());
    }
}