package no.nav.testnav.levendearbeidsforholdansettelse.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.service.AnsettelseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ansettelse")
@RequiredArgsConstructor
public class AnsettelseController {

    private final AnsettelseService ansettelseService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Starter en eksekvering på oppretting av arbeidsforhold")
    public void executeAnsettelser() {

        ansettelseService.runAnsettelseService();
    }
}
