package no.nav.registre.testnorge.arbeidsforhold.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.service.ArbeidsforholdService;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {
    private final ArbeidsforholdService service;

    @PostMapping
    public ResponseEntity<ArbeidsforholdDTO> createArbeidsforhold(@RequestBody ArbeidsforholdDTO dto) {
        Arbeidsforhold arbeidsforhold = service.createArbeidsforhold(new Arbeidsforhold(dto));
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ident}/{orgnummer}/{arbeidsforholdId}")
                .buildAndExpand(arbeidsforhold.getIdent(), arbeidsforhold.getOrgnummer(), arbeidsforhold.getArbeidsforholdId())
                .toUri();
        return ResponseEntity.created(uri).body(arbeidsforhold.toDTO());
    }

    @GetMapping("/{ident}/{orgnummer}/{arbeidsforholdId}")
    public ResponseEntity<ArbeidsforholdDTO> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("arbeidsforholdId") String arbeidsforholdId
    ) {
        var arbeidsforhold = service.getArbeidsforhold(ident, orgnummer, arbeidsforholdId);
        return ResponseEntity.ok(arbeidsforhold.toDTO());
    }

    @GetMapping("/{ident}/{orgnummer}/")
    public ResponseEntity<List<ArbeidsforholdDTO>> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer
    ) {
        var arbeidsforholdListe = service.getArbeidsforholds(ident, orgnummer);
        return ResponseEntity.ok(arbeidsforholdListe.stream().map(Arbeidsforhold::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{ident}")
    public ResponseEntity<List<ArbeidsforholdDTO>> getArbeidsforhold(@PathVariable("ident") String ident) {
        var arbeidsforholdListe = service.getArbeidsforholds(ident);
        return ResponseEntity.ok(arbeidsforholdListe.stream().map(Arbeidsforhold::toDTO).collect(Collectors.toList()));
    }
}
