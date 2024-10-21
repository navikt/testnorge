package no.nav.testnav.levendearbeidsforholdansettelse.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import no.nav.testnav.levendearbeidsforholdansettelse.service.LoggService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/logg")
@RequiredArgsConstructor
public class LoggController {

    private final LoggService loggService;

    @GetMapping
    @Operation(description = "Henter logger i hht forespørsel")
    public Mono<Page<AnsettelseLogg>> getAnsettelser(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "id") String... sort) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
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
