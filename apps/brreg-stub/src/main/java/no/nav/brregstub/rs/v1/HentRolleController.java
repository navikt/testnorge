package no.nav.brregstub.rs.v1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.HentRolleService;
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
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<Map<String, String>>> lagreEllerOppdaterHentRolleStub(@Valid @RequestBody RsOrganisasjon request) {
        return service.lagreEllerOppdaterDataForHentRolle(request)
                .map(result -> result
                        .map(org -> ResponseEntity.status(HttpStatus.CREATED).body(Map.of("path", API_V_1_HENTROLLE + "/" + request.getOrgnr())))
                        .orElseThrow(() -> new CouldNotCreateStubException("Kunne ikke opprette/oppdatere rolle")));
    }

    @GetMapping("/{orgnr}")
    public Mono<ResponseEntity<RsOrganisasjon>> hentGrunndata(@NotNull @PathVariable Integer orgnr) {
        return service.hentRolle(orgnr)
                .map(result -> result
                        .map(org -> ResponseEntity.status(HttpStatus.OK).body(org))
                        .orElseThrow(() -> new NotFoundException("Kunne ikke finne roller for :%s".formatted(orgnr))));
    }

    @DeleteMapping("/{orgnr}")
    public Mono<ResponseEntity<Void>> deleteGrunndata(@NotNull @PathVariable Integer orgnr) {
        return service.slettHentRolle(orgnr)
                .then(Mono.just(ResponseEntity.status(HttpStatus.OK).<Void>build()));
    }
}
