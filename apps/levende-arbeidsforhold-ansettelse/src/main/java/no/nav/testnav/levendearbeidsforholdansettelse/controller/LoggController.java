package no.nav.testnav.levendearbeidsforholdansettelse.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.testnav.levendearbeidsforholdansettelse.service.LoggService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/logg")
@RequiredArgsConstructor
public class LoggController {

    private final LoggService loggService;

    @GetMapping
    @Operation(description = "Henter logger i hht forespørsel")
    public Flux<Page<AnsettelseLogg>> getAnsettelser(Pageable pageable) {

        return loggService.getAnsettelseLogg(pageable);
    }

    @GetMapping("/ident/{ident}")
    @Operation(description = "Henter logg i hht forespørsel")
    public Flux<AnsettelseLogg> getIdent(@PathVariable String ident) {

        return loggService.getIdent(ident);
    }

    @GetMapping("/organisasjon/{orgnummer}")
    @Operation(description = "Henter logg i hht forespørsel")
    public Flux<AnsettelseLogg> getOrganisasjon(@PathVariable String orgnummer) {

        return loggService.getOrgnummer(orgnummer);
    }
}
