package no.nav.dolly.bestilling.aareg.amelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.OrganisasjonGetCommand;
import no.nav.dolly.config.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public OrganisasjonServiceConsumer(TokenExchange tokenService,
                                       OrganisasjonServiceProperties serviceProperties
    ) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
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