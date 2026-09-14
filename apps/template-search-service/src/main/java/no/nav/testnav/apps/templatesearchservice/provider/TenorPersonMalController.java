package no.nav.testnav.apps.templatesearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.OppdaterTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalOversiktResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalResponse;
import no.nav.testnav.apps.templatesearchservice.service.TenorPersonMalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/tenor/maler/personer")
@RequiredArgsConstructor
public class TenorPersonMalController {

    private final TenorPersonMalService malService;

    @PostMapping
    @Operation(description = "Opprett eller oppdater en Tenor-søkemal for innlogget bruker")
    public Mono<ResponseEntity<TenorPersonMalResponse>> save(
            @Valid @RequestBody OpprettTenorPersonMalRequest request
    ) {
        return malService.save(request)
                .map(result -> {
                    if (result.opprettet()) {
                        return ResponseEntity
                                .created(URI.create("/api/v1/tenor/maler/personer/" + result.mal().id()))
                                .body(result.mal());
                    }
                    return ResponseEntity.ok(result.mal());
                });
    }

    @GetMapping("/brukerId/{brukerId}")
    @Operation(description = "Hent tilgjengelige Tenor-søkemaler for angitt brukerId eller ALLE")
    public Flux<TenorPersonMalResponse> getMaler(@PathVariable String brukerId) {
        return malService.getMaler(brukerId);
    }

    @GetMapping("/oversikt")
    @Operation(description = "Hent oversikt over brukere med tilgjengelige Tenor-søkemaler")
    public Mono<TenorPersonMalOversiktResponse> getMalOversikt() {
        return malService.getMalOversikt();
    }

    @PatchMapping("/{id}")
    @Operation(description = "Endre navn på egen Tenor-søkemal")
    public Mono<TenorPersonMalResponse> updateMalNavn(
            @PathVariable Long id,
            @Valid @RequestBody OppdaterTenorPersonMalRequest request
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
