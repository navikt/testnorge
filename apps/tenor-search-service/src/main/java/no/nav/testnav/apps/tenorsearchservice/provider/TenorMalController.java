package no.nav.testnav.apps.tenorsearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.OpprettTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.OppdaterTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.service.TenorMalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/tenor/maler")
@RequiredArgsConstructor
public class TenorMalController {

    private final TenorMalService malService;

    @PostMapping
    @Operation(description = "Opprett eller oppdater en Tenor-søkemal for innlogget bruker")
    public Mono<ResponseEntity<TenorMalResponse>> save(
            @Valid @RequestBody OpprettTenorMalRequest request
    ) {
        return malService.save(request)
                .map(result -> {
                    if (result.opprettet()) {
                        return ResponseEntity
                                .created(URI.create("/api/v1/tenor/maler/" + result.mal().id()))
                                .body(result.mal());
                    }
                    return ResponseEntity.ok(result.mal());
                });
    }

    @GetMapping
    @Operation(description = "Hent tilgjengelige Tenor-søkemaler")
    public Flux<TenorMalResponse> getMaler(
            @RequestParam(required = false) String brukerId,
            @RequestParam(required = false) TenorMalType malType
    ) {
        return malService.getMaler(brukerId, malType);
    }

    @GetMapping("/brukere")
    @Operation(description = "Hent brukere med tilgjengelige Tenor-søkemaler")
    public Flux<TenorMalBrukerResponse> getBrukere() {
        return malService.getBrukere();
    }

    @PatchMapping("/{id}")
    @Operation(description = "Endre navn på egen Tenor-søkemal")
    public Mono<TenorMalResponse> updateMalNavn(
            @PathVariable Long id,
            @Valid @RequestBody OppdaterTenorMalRequest request
    ) {
        return malService.updateMalNavn(id, request.malNavn());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(description = "Slett egen Tenor-søkemal")
    public Mono<Void> delete(@PathVariable Long id) {
        return malService.delete(id);
    }
}
