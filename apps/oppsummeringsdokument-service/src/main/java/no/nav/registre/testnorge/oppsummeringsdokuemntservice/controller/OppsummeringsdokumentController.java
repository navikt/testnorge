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

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentetDTO;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter.OppsummeringsdokumentAdapter;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain.Oppsummeringsdokument;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oppsummeringsdokumenter")
public class OppsummeringsdokumentController {

    private final OppsummeringsdokumentAdapter adapter;

    @PutMapping
    public ResponseEntity<HttpStatus> save(@RequestBody OppsummeringsdokumentetDTO dto, @RequestHeader("miljo") String miljo) {
        var opplysningspliktig = new Oppsummeringsdokument(dto);
        var id = adapter.save(opplysningspliktig, miljo);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OppsummeringsdokumentetDTO> get(@PathVariable("id") String id) {
        var oppsummeringsdokument = adapter.get(id);
        if (oppsummeringsdokument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oppsummeringsdokument.toDTO());
    }

    @GetMapping("/{orgnummer}/{kalendermaaned}")
    public ResponseEntity<OppsummeringsdokumentetDTO> getOpplysningspliktigFromKalendermaaned(
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestParam("miljo") String miljo
    ) {
        var oppsummeringsdokument = adapter.getLastBy(kalendermaaned, orgnummer);
        if (oppsummeringsdokument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oppsummeringsdokument.toDTO());
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete() {
        adapter.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
