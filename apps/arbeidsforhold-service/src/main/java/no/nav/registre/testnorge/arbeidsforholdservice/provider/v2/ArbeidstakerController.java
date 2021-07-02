package no.nav.registre.testnorge.arbeidsforholdservice.provider.v2;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforholdservice.service.ArbeidsforholdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v2/arbeidstaker")
@RequiredArgsConstructor
public class ArbeidstakerController {

    private final ArbeidsforholdService arbeidsforholdService;

    @GetMapping("/{arbeidstaker}/arbeidsforhold")
    public ResponseEntity<?> getArbeidsforhold(
            @PathVariable("arbeidstaker") String ident,
            @RequestHeader("miljo") String miljo
    ) {
        var arbeidsforhold = arbeidsforholdService.getArbeidsforhold(ident, miljo);

        if (arbeidsforhold == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(arbeidsforhold.stream().map(Arbeidsforhold::toV2DTO).collect(Collectors.toList()));
    }
}
