package no.nav.registre.testnorge.arbeidsforhold.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregConsumer;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@RestController
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {
    private final AaregConsumer consumer;

    @GetMapping("/{ident}/{orgnummer}/{arbeidsforholdId}")
    public ResponseEntity<ArbeidsforholdDTO> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("arbeidsforholdId") String arbeidsforholdId
    ) {
        var arbeidsforhold = consumer.getArbeidsforhold(ident, orgnummer, arbeidsforholdId);
        return ResponseEntity.ok(arbeidsforhold.toDTO());
    }

    @GetMapping("/{ident}/{orgnummer}/")
    public ResponseEntity<List<ArbeidsforholdDTO>> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer
    ) {
        var arbeidsforholdListe = consumer.getArbeidsforholds(ident, orgnummer);
        return ResponseEntity.ok(arbeidsforholdListe.stream().map(Arbeidsforhold::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{ident}")
    public ResponseEntity<List<ArbeidsforholdDTO>> getArbeidsforhold(@PathVariable("ident") String ident) {
        var arbeidsforholdListe = consumer.getArbeidsforholds(ident);
        return ResponseEntity.ok(arbeidsforholdListe.stream().map(Arbeidsforhold::toDTO).collect(Collectors.toList()));
    }
}
