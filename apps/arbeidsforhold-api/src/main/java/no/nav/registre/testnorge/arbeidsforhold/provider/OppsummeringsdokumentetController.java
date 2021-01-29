package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.adapter.OppsummeringsdokumentetAdapter;
import no.nav.registre.testnorge.arbeidsforhold.domain.Oppsummeringsdokumentet;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OppsummeringsdokumentDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/oppsummeringsdokumenter")
@RequiredArgsConstructor
public class OppsummeringsdokumentetController {
    private final OppsummeringsdokumentetAdapter oppsummeringsdokumentetAdapter;

    @GetMapping
    public ResponseEntity<List<OppsummeringsdokumentDTO>> getAllOpplysningspliktig(
            @RequestHeader("miljo") String miljo
    ) {
        List<Oppsummeringsdokumentet> oppsummeringsdokumentets = oppsummeringsdokumentetAdapter.fetchAll(miljo);
        return ResponseEntity.ok(oppsummeringsdokumentets.stream().map(Oppsummeringsdokumentet::toDTO).collect(Collectors.toList()));
    }

    @PutMapping
    public ResponseEntity<HttpStatus> createOpplysningspliktig(
            @RequestBody OppsummeringsdokumentDTO opplysningspliktigDTO,
            @RequestHeader("miljo") String miljo
    ) {
        Oppsummeringsdokumentet opplysningspliktig = new Oppsummeringsdokumentet(opplysningspliktigDTO);
        oppsummeringsdokumentetAdapter.save(opplysningspliktig, miljo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orgnummer}/{kalendermaaned}")
                .buildAndExpand(opplysningspliktig.getOrgnummer(), opplysningspliktig.getRapporteringsmaaned())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{orgnummer}/{kalendermaaned}")
    public ResponseEntity<OppsummeringsdokumentDTO> getOpplysningspliktigFromKalendermaaned(
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestHeader("miljo") String miljo
    ) {
        Oppsummeringsdokumentet opplysningspliktig = oppsummeringsdokumentetAdapter.fetch(orgnummer, kalendermaaned, miljo);
        if (opplysningspliktig == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opplysningspliktig.toDTO());
    }
}