package no.nav.registre.testnorge.levendearbeidsforhold.provider.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.service.ArbeidsforholdService;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {
    private final ArbeidsforholdService arbeidsforholdService;

    @GetMapping("/{ident}/{orgnummer}/{arbeidsforholdId}")
    public void getArbeidsforhold(
            @PathVariable("ident") String ident

    ) {
        var arbeidsforhold = arbeidsforholdService.getArbeidsforhold(ident);
        log.info("Arbeidsforhold: " + arbeidsforhold.toString());

    }
}
