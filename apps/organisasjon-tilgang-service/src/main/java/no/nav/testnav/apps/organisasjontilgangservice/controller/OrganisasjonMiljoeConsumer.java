package no.nav.testnav.apps.organisasjontilgangservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.apps.organisasjontilgangservice.database.repository.OrganisasjonTilgangRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class OrganisasjonMiljoeConsumer {

    private final OrganisasjonTilgangRepository organisasjonTilgangRepository;

    @GetMapping("/organisasjon/{orgnummer}")
    public Mono<OrganisasjonTilgang> getOrganisasjon(@RequestParam("orgnummer") String orgnummer) {

        return organisasjonTilgangRepository.findByOrganisasjonNummer(orgnummer);
    }
}
