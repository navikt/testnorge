package no.nav.testnav.altinn3tilgangservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.domain.OrganisasjonResponse;
import no.nav.testnav.altinn3tilgangservice.service.AltinnOrganisasjonTilgangService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Slf4j
@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class AltinnOrganisasjonTilgangController {

    private final AltinnOrganisasjonTilgangService altinnTilgangService;

    @GetMapping
    @Operation(description = "Henter alle organisasjoner med Altinn-tilgang")
    public Flux<OrganisasjonResponse> getAll() {

        return  altinnTilgangService.getAll()
                .sort(Comparator.comparing(OrganisasjonResponse::getNavn));
    }

    @PostMapping("/{organisasjonsnummer}")
    @Operation(description = "Oppretter Altinn-tilgang for organisasjon")
    public Mono<OrganisasjonResponse> create(@PathVariable String organisasjonsnummer,
                                             @RequestParam String miljoe) {

        return altinnTilgangService
                .create(organisasjonsnummer, miljoe);
    }

    @DeleteMapping("/{organisasjonsnummer}")
    @Operation(description = "Sletter Altinn-tilgang for organisasjon")
    public Flux<OrganisasjonResponse> delete(@PathVariable String organisasjonsnummer) {

        return altinnTilgangService.delete(organisasjonsnummer);
    }
}
