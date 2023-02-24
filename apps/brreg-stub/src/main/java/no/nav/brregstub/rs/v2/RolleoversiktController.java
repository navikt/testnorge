package no.nav.brregstub.rs.v2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.RolleoversiktService;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v2/rolleoversikt")
@AllArgsConstructor
public class RolleoversiktController {

    private final RolleoversiktService service;

    @PostMapping
    public ResponseEntity<RsRolleoversikt> lagreEllerOppdaterRolleoversikt(@Valid @RequestBody RsRolleoversikt rolleoversikt) {
        var grunndata = service.opprettRolleoversiktV2(rolleoversikt)
                .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette rolleoversikt"));
        return ResponseEntity.status(HttpStatus.CREATED).body(grunndata);
    }

    @GetMapping
    public ResponseEntity<RsRolleoversikt> hentRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = service.hentRolleoversiktV2(ident)
                .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s", ident)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @DeleteMapping
    public ResponseEntity slettRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        service.slettRolleoversikt(ident);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
