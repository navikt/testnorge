package no.nav.testnav.apps.organisasjontilgangservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjontilgangservice.controller.request.OrganisasjonAccessRequest;
import no.nav.testnav.apps.organisasjontilgangservice.domain.OrganisasjonResponse;
import no.nav.testnav.apps.organisasjontilgangservice.service.OrganisasjonTilgangService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class OrganisasjonTilgangConsumer {

    private final OrganisasjonTilgangService organisasjonTilgangService;

    @GetMapping
    public Flux<OrganisasjonResponse> getAll() {

        return organisasjonTilgangService.getAll();
    }

    @PostMapping
    public Mono<OrganisasjonResponse> create(@RequestBody OrganisasjonAccessRequest request) {

        return organisasjonTilgangService
                .create(request.organisasjonsnummer(), request.gyldigTil(), request.miljoe());
    }

    @DeleteMapping("/{organisasjonsnummer}")
    public Flux<Void> delete(@PathVariable String organisasjonsnummer) {

        return organisasjonTilgangService.delete(organisasjonsnummer);
    }

    @PutMapping
    public Mono<OrganisasjonResponse> update(@RequestBody OrganisasjonAccessRequest request) {

        organisasjonTilgangService.delete(request.organisasjonsnummer()).blockFirst();
        return organisasjonTilgangService
                .create(request.organisasjonsnummer(), request.gyldigTil(), request.miljoe());
    }
}
