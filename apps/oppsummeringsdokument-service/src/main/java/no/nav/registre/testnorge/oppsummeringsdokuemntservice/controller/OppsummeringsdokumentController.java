package no.nav.registre.testnorge.oppsummeringsdokuemntservice.controller;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter.OppsummeringsdokumentAdapter;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain.Oppsummeringsdokument;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oppsummeringsdokumenter")
public class OppsummeringsdokumentController {

    private final OppsummeringsdokumentAdapter adapter;

    @GetMapping
    public ResponseEntity<List<OppsummeringsdokumentDTO>> getAll(@RequestHeader("miljo") String miljo){
        var documents = adapter.getAllCurrentDocumentsBy(miljo);
        return ResponseEntity.ok(documents.stream().map(Oppsummeringsdokument::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OppsummeringsdokumentDTO> get(@PathVariable("id") String id) {
        var oppsummeringsdokument = adapter.get(id);
        if (oppsummeringsdokument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oppsummeringsdokument.toDTO());
    }

    @GetMapping("/{orgnummer}/{kalendermaaned}")
    public ResponseEntity<OppsummeringsdokumentDTO> getOpplysningspliktigFromKalendermaaned(
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestHeader("miljo") String miljo
    ) {
        var oppsummeringsdokument = adapter.getCurrentDocumentBy(kalendermaaned, orgnummer, miljo);
        if (oppsummeringsdokument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oppsummeringsdokument.toDTO());
    }

    @PutMapping
    public ResponseEntity<HttpStatus> save(
            @RequestBody OppsummeringsdokumentDTO dto,
            @RequestHeader("miljo") String miljo,
            @RequestHeader("origin") String origin
    ) {
        var opplysningspliktig = new Oppsummeringsdokument(dto);
        var id = adapter.save(opplysningspliktig, miljo, origin);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete() {
        adapter.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
