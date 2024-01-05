package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service.OrkestratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/arbeidsforhold")
public class GenererArbeidsforholdController {

    private final OrkestratorService orkestratorService;

    @PostMapping(value = "/develop")
    public void populate(
            @RequestHeader String miljo,
            @RequestParam Integer months
    ) {
        orkestratorService.orkestrerMedArbeidsforhold(miljo, months);
    }
}
