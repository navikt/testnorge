package no.nav.dolly.bestilling.yrkesskade;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.yrkesskade.command.YrkesskadePostCommand;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class YrkesskadeConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public YrkesskadeConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getYrkesskadeProxy();
        webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<ResponseEntity<String>> lagreYrkesskade(YrkesskadeRequest request) {

        log.info("Sender yrkesskade melding: {}", request);
        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> new YrkesskadePostCommand(webClient, request, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Mottatt response fra yrkesskade service {}", response));
    }
}
