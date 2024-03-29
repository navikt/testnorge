package no.nav.testnav.apps.oppsummeringsdokumentservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oppsummeringsdokumentservice.service.OppsummeringsdokumentService;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oppsummeringsdokumenter")
public class OppsummeringsdokumentController {

    private final OppsummeringsdokumentService adapter;

    @GetMapping
    public List<OppsummeringsdokumentDTO> getAll(
            @RequestHeader("miljo") String miljo,
            @RequestParam("page") Integer page) {

        var documents = adapter.getAllCurrentDocumentsBy(miljo, page);
        return documents.stream()
                .map(Oppsummeringsdokument::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OppsummeringsdokumentDTO> get(@PathVariable("id") String id) {

        var oppsummeringsdokument = adapter.getCurrentDocumentsBy(id);
        return oppsummeringsdokument.map(value -> ResponseEntity.ok(value.toDTO()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{orgnummer}/{kalendermaaned}")
    public ResponseEntity<OppsummeringsdokumentDTO> getOpplysningspliktigFromKalendermaaned(
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestHeader("miljo") String miljo) {

        var oppsummeringsdokument = adapter.getCurrentDocumentBy(kalendermaaned, orgnummer, miljo);
        return oppsummeringsdokument.map(value -> ResponseEntity.ok().body(value.toDTO()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> save(
            @RequestBody OppsummeringsdokumentDTO dto,
            @RequestHeader("miljo") String miljo,
            @RequestHeader("origin") String origin,
            @RequestHeader(required = false) Populasjon populasjon
    ) {
        var previous = adapter.getCurrentDocumentBy(dto.getKalendermaaned(), dto.getOpplysningspliktigOrganisajonsnummer(), miljo);

        if (previous.isPresent() && previous.get().getVersion().equals(dto.getVersion())) {

            return ResponseEntity
                    .badRequest()
                    .body(String.format(
                            "Oppsummeringsdokument for %s den %s med version %s i %s finnes allerde. Bump versjonen.",
                            dto.getOpplysningspliktigOrganisajonsnummer(),
                            dto.getKalendermaaned(),
                            dto.getVersion(),
                            miljo
                    ));
        }

        var opplysningspliktig = new Oppsummeringsdokument(dto, populasjon);

        var id = adapter.save(opplysningspliktig, miljo, origin);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).header("ID", id).build();
    }

    @GetMapping("/identer/{ident}")
    public List<OppsummeringsdokumentDTO> getIdent(@RequestHeader("miljo") String miljo,
                                                   @PathVariable("ident") String ident) {

        var documents = adapter.getAllCurrentDocumentsBy(miljo, ident);
        return documents.stream()
                .map(Oppsummeringsdokument::toDTO)
                .toList();
    }

    @GetMapping("/identer")
    public Set<String> getIdenter(
            @RequestHeader("miljo") String miljo,
            @RequestHeader(required = false) Populasjon populasjon) {

        return adapter.getAllCurrentDocumentsBy(miljo)
                .stream()
                .flatMap(document -> document.getIdenter().stream())
                .collect(Collectors.toSet());
    }
}
