package no.nav.registre.testnorge.arbeidsforholdservice.provider.v1;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.service.ArbeidsforholdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {
    private final ArbeidsforholdService arbeidsforholdService;

    @GetMapping("/{ident}/{orgnummer}/{arbeidsforholdId}")
    public ResponseEntity<?> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("arbeidsforholdId") String arbeidsforholdId,
            @RequestHeader("miljo") String miljo
    ) {
        var arbeidsforhold = arbeidsforholdService.getArbeidsforhold(ident, orgnummer, arbeidsforholdId, miljo);
        return arbeidsforhold
                .map(value -> ResponseEntity.ok(value.toV1DTO()))
                .orElse(ResponseEntity.notFound().build());
    }

}
