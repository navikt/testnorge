package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.LoggService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logg")
@RequiredArgsConstructor
public class LoggController {

    private final LoggService loggService;

    @GetMapping
    @Operation(description = "Henter logger i hht forespørsel")
    public Page<AnsettelseLogg> getAnsettelser(Pageable pageable) {

        return loggService.getAnsettelseLogg(pageable);
    }
}
