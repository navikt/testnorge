package no.nav.brregstub.rs.v1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.HentRolleService;

@Validated
@Slf4j
@RestController
@RequestMapping(HentRolleController.API_V_1_HENTROLLE)
@AllArgsConstructor
public class HentRolleController {

    public static final String API_V_1_HENTROLLE = "/api/v1/hentrolle";
    private final HentRolleService service;

    @PostMapping
    public ResponseEntity<Map> lagreEllerOppdaterHentRolleStub(@Valid @RequestBody RsOrganisasjon request) {
        var organisasjonTo = service.lagreEllerOppdaterDataForHentRolle(request)
                .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette/oppdatere rolle"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("path", API_V_1_HENTROLLE + "/" + request.getOrgnr()));
    }

    @GetMapping("/{orgnr}")
    public ResponseEntity<RsOrganisasjon> hentGrunndata(@NotNull @PathVariable Integer orgnr) {
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
