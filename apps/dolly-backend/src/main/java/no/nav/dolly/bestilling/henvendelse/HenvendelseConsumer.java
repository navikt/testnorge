package no.nav.dolly.bestilling.henvendelse;

import no.nav.dolly.bestilling.henvendelse.command.HenvendelsePostCommand;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HenvendelseConsumer {



    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public HenvendelseConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<HenvendelseResponse> sendHenvendelse(HenvendelseSamtalereferatRequest melding) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HenvendelsePostCommand(webClient, melding, token.getTokenValue()).call());
    }
}
