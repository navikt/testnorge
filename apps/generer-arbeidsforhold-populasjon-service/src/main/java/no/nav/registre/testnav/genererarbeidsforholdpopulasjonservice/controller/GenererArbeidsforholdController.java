package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Person;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service.OrkestratorService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/arbeidsforhold")
public class GenererArbeidsforholdController {

    private final OrkestratorService orkestratorService;

    @PostMapping(value = "/populate")
    public void populate(
            @RequestHeader String miljo,
            @RequestHeader Integer max,
            @RequestParam("fom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam("tom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom
    ) {
        orkestratorService.orkester(max, miljo, fom, tom);
    }

}
