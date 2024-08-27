package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.LoggService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/ident/{ident}")
    @Operation(description = "Henter logg i hht forespørsel")
    public List<AnsettelseLogg> getIdent(@PathVariable String ident) {

        return loggService.getIdent(ident);
    }

    @GetMapping("/organisasjon/{orgnummer}")
    @Operation(description = "Henter logg i hht forespørsel")
    public List<AnsettelseLogg> getOrganisasjon(@PathVariable String orgnummer) {

        return loggService.getOrgnummer(orgnummer);
    }
}
