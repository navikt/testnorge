package no.nav.dolly.bestilling.nom;

import no.nav.dolly.bestilling.nom.command.NomAvsluttRessurs;
import no.nav.dolly.bestilling.nom.command.NomHentRessurs;
import no.nav.dolly.bestilling.nom.command.NomOpprettRessurs;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.bestilling.nom.domain.NomRessursResponse;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NomConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public NomConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getNomProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<NomRessursResponse> hentRessurs(NomRessursRequest nomRessursRequest) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomHentRessurs(webClient, nomRessursRequest, token.getTokenValue()).call());
    }

    public Mono<NomRessursResponse> opprettRessurs(NomRessursRequest nomRessursRequest) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomOpprettRessurs(webClient, nomRessursRequest, token.getTokenValue()).call());
    }

    public Mono<NomRessursResponse> avsluttRessurs(NomRessursRequest nomRessursRequest) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new NomAvsluttRessurs(webClient, nomRessursRequest, token.getTokenValue()).call());
    }
}
