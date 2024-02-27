package no.nav.testnav.apps.oppsummeringsdokumentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.QueryRequest;
import no.nav.testnav.apps.oppsummeringsdokumentservice.service.OppsummeringsdokumentService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter/raw/items")
@RequiredArgsConstructor
public class OppsummeringsdokumentetRawController {
    private static final String NUMBER_OF_ELEMENTS_HEADER = "Number-Of-Elements";
    private static final String ELEMENT_ID_HEADER = "Element-Id";
    private final OppsummeringsdokumentService oppsummeringsdokumentService
            ;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo,
            @RequestParam("page") Integer page,
            @RequestParam(value = "ident", required = false) String ident,
            @RequestParam(value = "typeArbeidsforhold", required = false) String typeArbeidsforhold,
            @RequestParam(value = "fom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam(value = "tom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom) {

        var response = oppsummeringsdokumentService.getAllCurrentDocumentsBy(QueryRequest.builder()
                .miljo(miljo)
                .fom(fom)
                .tom(tom)
                .ident(ident)
                .typeArbeidsforhold(typeArbeidsforhold)
                .page(page)
                .pageSize(1)
                .build());

        return response.getDokumenter().isEmpty() ?
                ResponseEntity
                        .noContent()
                        .header(NUMBER_OF_ELEMENTS_HEADER, "0")
                        .build() :

                ResponseEntity.ok()
                        .header(NUMBER_OF_ELEMENTS_HEADER, String.valueOf(response.getResponse().getHits().getTotalHits()))
                        .header(ELEMENT_ID_HEADER, response.getDokumenter().getFirst().getId())
                        .body(response.getDokumenter().getFirst().toXml());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getItem(@PathVariable("id") String id) {

        var document = oppsummeringsdokumentService.getCurrentDocumentsBy(id);

        return document.map(value -> ResponseEntity.ok().body(value.toXml()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}