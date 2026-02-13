package no.nav.brregstub.rs.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.v2.RsRolleoversikt;
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
@RequestMapping("/api/v2/rolleoversikt")
@AllArgsConstructor
public class RolleoversiktController {

    private final RolleoversiktService service;

    @PostMapping
    public Mono<ResponseEntity<RsRolleoversikt>> lagreEllerOppdaterRolleoversikt(@Valid @RequestBody RsRolleoversikt rolleoversikt) {
        return service.opprettRolleoversiktV2(rolleoversikt)
                .map(result -> result
                        .map(data -> ResponseEntity.status(HttpStatus.CREATED).body(data))
                        .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette rolleoversikt")));
    }

    @GetMapping
    public Mono<ResponseEntity<RsRolleoversikt>> hentRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        return service.hentRolleoversiktV2(ident)
                .map(result -> result
                        .map(data -> ResponseEntity.status(HttpStatus.OK).body(data))
                        .orElseThrow(() -> new NotFoundException("Kunne ikke finne person med fnr:%s".formatted(ident))));
    }

    @DeleteMapping
    public Mono<Void> slettRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        return service.slettRolleoversikt(ident);
    }
}
