package no.nav.brregstub.rs;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.RolleutskriftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/rolleutskrift")
@AllArgsConstructor
public class HentRolleutskriftController {

    private final RolleutskriftService service;


    @PostMapping
    public ResponseEntity<RolleutskriftTo> opprettGrunndata(@RequestBody RolleutskriftTo rolleinnhaver) {
        var grunndata = service.opprettRolleutskriftGrunndata(rolleinnhaver)
                               .orElseThrow(() -> new CouldNotCreateStubException(""));
        return ResponseEntity.status(HttpStatus.CREATED).body(grunndata);
    }

    @GetMapping
    public ResponseEntity<RolleutskriftTo> hentGrunndata(@RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = service.hentRolleinnhaverTo(ident)
                               .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s",
                                                                                           ident)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @DeleteMapping
    public ResponseEntity deleteGrunndata(@RequestHeader(name = "Nav-Personident") String ident) {
        service.slettRolleutskriftGrunndata(ident);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
