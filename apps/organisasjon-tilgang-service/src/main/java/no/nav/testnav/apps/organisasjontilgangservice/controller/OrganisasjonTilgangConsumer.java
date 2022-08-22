package no.nav.testnav.apps.organisasjontilgangservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.organisasjontilgangservice.controller.dto.OrganisasjonDTO;
import no.nav.testnav.apps.organisasjontilgangservice.controller.request.OrganisasjonAccessRequest;
import no.nav.testnav.apps.organisasjontilgangservice.domain.Organisasjon;
import no.nav.testnav.apps.organisasjontilgangservice.service.OrganisasjonTilgangService;

@Slf4j
@RestController
@RequestMapping("/api/v1/organisasjoner")
@RequiredArgsConstructor
public class OrganisasjonTilgangConsumer {

    private final OrganisasjonTilgangService service;

    @GetMapping
    public Flux<OrganisasjonDTO> getAll() {
        return service.getAll()
                .map(Organisasjon::toDTO);
    }

    @PostMapping
    public Mono<OrganisasjonDTO> create(@RequestBody OrganisasjonAccessRequest request) {
        return service
                .create(request.organisasjonsnummer(), request.gyldigTil())
                .map(Organisasjon::toDTO);
    }

    @DeleteMapping("/{organisasjonsnummer}")
    public Flux<Void> delete(@PathVariable String organisasjonsnummer) {
        return service.delete(organisasjonsnummer);
    }

}
