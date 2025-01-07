package no.nav.testnav.apps.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.AltinnTilgangServiceConsumer;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/person-organisasjoner")
@RequiredArgsConstructor
public class PersonOrganisasjonController {

    private final AltinnTilgangServiceConsumer altinnTilgangServiceConsumer;

    @GetMapping
    public Flux<OrganisasjonDTO> getOrganisasjoner() {

        return altinnTilgangServiceConsumer.getOrganisasjoner();
    }
}
