package no.nav.testnav.apps.tilgangservice.controller;

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

import no.nav.testnav.apps.tilgangservice.controller.dto.OrganisajonDTO;
import no.nav.testnav.apps.tilgangservice.controller.request.OrganiasjonAccessRequest;
import no.nav.testnav.apps.tilgangservice.domain.Organisajon;
import no.nav.testnav.apps.tilgangservice.service.OrganiasjonTilgangService;

@Slf4j
@RestController
@RequestMapping("/api/v1/organisajoner")
@RequiredArgsConstructor
public class OrganiasjonTilgangConsumer {

    private final OrganiasjonTilgangService service;

    @GetMapping
    public Flux<OrganisajonDTO> getAll() {
        return service.getAll()
                .map(Organisajon::toDTO);
    }

    @PostMapping
    public Mono<OrganisajonDTO> create(@RequestBody OrganiasjonAccessRequest request) {
        return service
                .create(request.organisajonsnummer(), request.gyldigTil())
                .map(Organisajon::toDTO);
    }

    @DeleteMapping("/{organisajonsnummer}")
    public Flux<Void> delete(@PathVariable String organisajonsnummer) {
        return service.delete(organisajonsnummer);
    }

}
