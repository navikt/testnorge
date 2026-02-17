package no.nav.brregstub.rs.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.RolleoversiktService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/rolleoversikt")
@AllArgsConstructor
public class HentRolleoversiktController {

    private final RolleoversiktService service;

    @PostMapping
    public Mono<ResponseEntity<RolleoversiktTo>> lagreEllerOppdaterRolleoversikt(@Valid @RequestBody RolleoversiktTo rolleoversikt) {
        return service.opprettRolleoversiktV1(rolleoversikt)
                .map(result -> result
                        .map(data -> ResponseEntity.status(HttpStatus.CREATED).body(data))
                        .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette rolleoversikt")));
    }

    @GetMapping
    public Mono<ResponseEntity<RolleoversiktTo>> hentRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        return service.hentRolleoversiktV1(ident)
                .map(result -> result
                        .map(data -> ResponseEntity.status(HttpStatus.OK).body(data))
                        .orElseThrow(() -> new NotFoundException("Kunne ikke finne person med fnr:%s".formatted(ident))));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> slettRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        return service.slettRolleoversikt(ident)
                .then(Mono.just(ResponseEntity.status(HttpStatus.OK).<Void>build()));
    }

}
