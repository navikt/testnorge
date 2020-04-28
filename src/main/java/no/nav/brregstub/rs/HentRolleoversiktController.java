package no.nav.brregstub.rs;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.RolleoversiktTo;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.RolleoversiktService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/rolleoversikt")
@AllArgsConstructor
public class HentRolleoversiktController {

    private final RolleoversiktService service;


    @PostMapping
    public ResponseEntity<RolleoversiktTo> lagreEllerOppdaterRolleoversikt(@Valid @RequestBody RolleoversiktTo rolleoversikt) {
        var grunndata = service.opprettRolleoversikt(rolleoversikt)
                               .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette rolleoversikt"));
        return ResponseEntity.status(HttpStatus.CREATED).body(grunndata);
    }

    @GetMapping
    public ResponseEntity<RolleoversiktTo> hentRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = service.hentRolleoversikt(ident)
                               .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s",
                                                                                           ident)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @DeleteMapping
    public ResponseEntity slettRolleoversikt(@NotNull @RequestHeader(name = "Nav-Personident") String ident) {
        service.slettRolleoversikt(ident);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
