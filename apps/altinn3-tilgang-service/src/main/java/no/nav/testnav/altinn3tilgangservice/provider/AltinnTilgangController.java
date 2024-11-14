package no.nav.testnav.altinn3tilgangservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.domain.OrganisasjonResponse;
import no.nav.testnav.altinn3tilgangservice.service.AltinnTilgangService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class AltinnTilgangController {

    private final AltinnTilgangService altinnTilgangService;

    @GetMapping
    public Flux<OrganisasjonResponse> getAll() {

        return altinnTilgangService.getAll();
    }

    @PostMapping("/{organisasjonsnummer}")
    public Mono<OrganisasjonResponse> create(@PathVariable String organisasjonsnummer,
                                             @RequestParam String miljoe) {

        return altinnTilgangService
                .create(organisasjonsnummer, miljoe);
    }

    @DeleteMapping("/{organisasjonsnummer}")
    public Flux<OrganisasjonResponse> delete(@PathVariable String organisasjonsnummer) {

        return altinnTilgangService.delete(organisasjonsnummer);
    }
}
