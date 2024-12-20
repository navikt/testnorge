package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.Altinn3PersonOrganisasjonTilgangConsumer;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/altinn")
@RequiredArgsConstructor
public class BrukerTilgangController {

    private final Altinn3PersonOrganisasjonTilgangConsumer altinn3PersonOrganisasjonTilgangConsumer;

    @GetMapping("/organisasjoner")
    public Mono<List<OrganisasjonDTO>> getOrganisasjoner(ServerWebExchange exchange) {

        return altinn3PersonOrganisasjonTilgangConsumer.getOrganisasjoner(exchange)
                .collectList();
    }
}
