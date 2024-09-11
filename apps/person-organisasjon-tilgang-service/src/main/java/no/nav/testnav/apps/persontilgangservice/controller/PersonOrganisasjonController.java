package no.nav.testnav.apps.persontilgangservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.persontilgangservice.controller.dto.OrganisasjonDTO;
import no.nav.testnav.apps.persontilgangservice.domain.Access;
import no.nav.testnav.apps.persontilgangservice.domain.AccessToken;
import no.nav.testnav.apps.persontilgangservice.service.PersonOrganisasjonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/person/organisasjoner")
@RequiredArgsConstructor
public class PersonOrganisasjonController {

    private final PersonOrganisasjonService personOrganisasjonService;

    @GetMapping
    public Flux<OrganisasjonDTO> getOrganiasjoner() {
        return personOrganisasjonService.getAccess().map(Access::toDTO);
    }

    @GetMapping("/token")
    public Mono<AccessToken> getMaskinportenToken() {
        return personOrganisasjonService.getAccessToken();
    }


    @GetMapping("/{organisasjonsnummer}")
    public Mono<ResponseEntity<OrganisasjonDTO>> getOrganiasjoner(@PathVariable String organisasjonsnummer) {
        return personOrganisasjonService
                .getAccess(organisasjonsnummer)
                .map(Access::toDTO)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}