package no.nav.testnav.altinn3tilgangservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.altinn3tilgangservice.service.MiljoerOversiktService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class OrganisasjonMiljoeController {

    private final MiljoerOversiktService miljoerOversiktService;

    @GetMapping("/organisasjon/{organisasjonsnummer}")
    @Operation(description = "Henter miljøer for organisasjon")
    public Mono<OrganisasjonTilgang> getOrganisasjon(@PathVariable("organisasjonsnummer") String orgnummer) {

            return miljoerOversiktService.getMiljoe(orgnummer);
    }

    @PutMapping("/organisasjon/{organisasjonsnummer}")
    @Operation(description = "Endrer miljøer for organisasjon")
    public Mono<OrganisasjonTilgang> updateOrganisasjon(@PathVariable("organisasjonsnummer") String orgnummer,
                                                        @RequestParam String miljoe) {

        return miljoerOversiktService.updateMiljoe(orgnummer, miljoe);
    }
}
