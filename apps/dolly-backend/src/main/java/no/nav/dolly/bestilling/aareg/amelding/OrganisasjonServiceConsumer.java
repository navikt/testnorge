package no.nav.dolly.bestilling.aareg.amelding;

import no.nav.dolly.bestilling.aareg.command.OrganisasjonGetCommand;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;

@Service
public class OrganisasjonServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public OrganisasjonServiceConsumer(
            TokenExchange tokenService,
            Consumers.OrganisasjonService serviceProperties,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = webClientBuilder
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(orgnummerListe)
                        .flatMap(orgnummer ->
                                new OrganisasjonGetCommand(webClient, orgnummer, miljo, token.getTokenValue()).call()));
    }
}