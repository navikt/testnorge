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

import no.nav.registre.testnorge.arbeidsforhold.adapter.OpplysningspliktigAdapter;
import no.nav.registre.testnorge.arbeidsforhold.domain.Opplysningspliktig;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/opplysningspliktig")
@RequiredArgsConstructor
public class OpplysningspliktigController {
    private final OpplysningspliktigAdapter opplysningspliktigAdapter;

    @PutMapping
    public ResponseEntity<HttpStatus> createOpplysningspliktig(
            @RequestBody OpplysningspliktigDTO opplysningspliktigDTO,
            @RequestHeader("miljo") String miljo
    ) {
        Opplysningspliktig opplysningspliktig = new Opplysningspliktig(opplysningspliktigDTO);
        opplysningspliktigAdapter.save(opplysningspliktig, miljo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orgnummer}/{kalendermaaned}")
                .buildAndExpand(opplysningspliktig.getOrgnummer(), opplysningspliktig.getRapporteringsmaaned())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{orgnummer}/{kalendermaaned}")
    public ResponseEntity<OpplysningspliktigDTO> getOpplysningspliktigFromKalendermaaned(
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestHeader("miljo") String miljo
    ) {
        Opplysningspliktig opplysningspliktig = opplysningspliktigAdapter.fetch(orgnummer, kalendermaaned, miljo);
        if (opplysningspliktig == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opplysningspliktig.toDTO());
    }

    @GetMapping("/{orgnummer}")
    public ResponseEntity<List<OpplysningspliktigDTO>> getOpplysningspliktig(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        List<OpplysningspliktigDTO> opplysningspliktig = opplysningspliktigAdapter.fetch(orgnummer, miljo)
                .stream()
                .map(Opplysningspliktig::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(opplysningspliktig);
    }
}