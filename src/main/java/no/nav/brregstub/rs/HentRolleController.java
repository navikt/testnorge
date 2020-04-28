package no.nav.brregstub.rs;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.HentRolleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping(HentRolleController.API_V_1_HENTROLLE)
@AllArgsConstructor
public class HentRolleController {

    public static final String API_V_1_HENTROLLE = "/api/v1/hentrolle";
    private final HentRolleService service;


    @PostMapping
    public ResponseEntity<Map> lagreEllerOppdaterHentRolleStub(@Valid @RequestBody OrganisasjonTo request) {
        var organisasjonTo = service.lagreEllerOppdaterDataForHentRolle(request)
                                    .orElseThrow(() -> new CouldNotCreateStubException(""));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("path",API_V_1_HENTROLLE + "/" + request.getOrgnr()));
    }

    @GetMapping("/{orgnr}")
    public ResponseEntity<OrganisasjonTo> hentGrunndata(@NotNull @PathVariable Integer orgnr) {
        var grunndata = service.hentRolle(orgnr)
                               .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne roller for :%s",
                                                                                      orgnr)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @DeleteMapping("/{orgnr}")
    public ResponseEntity deleteGrunndata(@NotNull @PathVariable Integer orgnr) {
        service.slettHentRolle(orgnr);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
