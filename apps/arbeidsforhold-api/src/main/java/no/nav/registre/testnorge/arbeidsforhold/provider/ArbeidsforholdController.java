package no.nav.registre.testnorge.arbeidsforhold.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregConsumer;
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
}
