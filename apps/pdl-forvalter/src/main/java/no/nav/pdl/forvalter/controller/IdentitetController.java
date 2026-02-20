package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.service.IdentitetService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/identiteter")
@RequiredArgsConstructor
public class IdentitetController {

    private final IdentitetService identitetService;

    @ResponseBody
    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Søk etter identitet i databasen basert på fragment av ident og/eller en eller flere navn")
    public Mono<List<PersonIDDTO>> getPerson(@Parameter(description = "Fragment av ident og/eller en eller flere navn")
                                              @RequestParam(required = false) String fragment,
                                              @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                              @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                              @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først")
                                              @RequestParam(required = false, defaultValue = "10") Integer sidestorrelse) {

        return Mono.fromCallable(() -> identitetService.getfragment(fragment, Paginering.builder()
                        .sidenummer(sidenummer)
                        .sidestoerrelse(sidestorrelse)
                        .build()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(value = "/{ident}/standalone/{standalone}")
    @Operation(description = "Oppdaterer angitt person med standalone satt (true/false).<br>" +
            "Når satt, blir dette håndtert som en ekstern person som ikke skal inkluderes ved sletting.")
    public Mono<Void> updateStandalone(@Parameter(description = "Ident for testperson")
                                        @PathVariable String ident,
                                        @PathVariable Boolean standalone) {

        return Mono.<Void>fromRunnable(() -> identitetService.updateStandalone(ident, standalone))
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(ObjectOptimisticLockingFailureException.class::isInstance)
                        .doBeforeRetry(signal -> log.warn("Optimistic lock conflict for ident {}, forsøk {}", ident, signal.totalRetries() + 1)));
    }
}