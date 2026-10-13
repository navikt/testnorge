package no.nav.dolly.bestilling.henvendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.henvendelse.command.HenvendelseDeleteCommand;
import no.nav.dolly.bestilling.henvendelse.command.HenvendelseGetCommand;
import no.nav.dolly.bestilling.henvendelse.command.HenvendelsePostCommand;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
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
                .flatMap(token -> new HenvendelsePostCommand(webClient, melding, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Henvendelse sendt med respons: {} ", response));
    }

    public Mono<HenvendelseResponse> getHenvendelse(String aktorid) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HenvendelseGetCommand(webClient, aktorid, token.getTokenValue()).call());
    }

    public Mono<HenvendelseResponse> deleteHenvendelse(String kjedeId) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HenvendelseDeleteCommand(webClient, kjedeId, token.getTokenValue()).call());
    }
}
