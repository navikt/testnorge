package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.Altinn3PersonOrganisasjonTilgangConsumer;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/altinn")
@RequiredArgsConstructor
public class BrukerTilgangController {

    private final Altinn3PersonOrganisasjonTilgangConsumer altinn3PersonOrganisasjonTilgangConsumer;

    @GetMapping("/organisasjoner")
    public Flux<OrganisasjonDTO> getOrganisasjoner(ServerWebExchange exchange) {

        exchange.getAttributes()
                .forEach((key, value) -> log.info("Atributt {}: {}", key, value));

        return altinn3PersonOrganisasjonTilgangConsumer.getOrganisasjoner(exchange);
    }
}
