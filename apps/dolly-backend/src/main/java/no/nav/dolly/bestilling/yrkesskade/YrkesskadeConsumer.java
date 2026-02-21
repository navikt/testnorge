package no.nav.dolly.bestilling.yrkesskade;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.yrkesskade.command.YrkesskadeGetCommand;
import no.nav.dolly.bestilling.yrkesskade.command.YrkesskadePostCommand;
import no.nav.dolly.bestilling.yrkesskade.dto.SaksoversiktDTO;
import no.nav.dolly.bestilling.yrkesskade.dto.YrkesskadeResponseDTO;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class YrkesskadeConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public YrkesskadeConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient) {

        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getYrkesskadeProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<YrkesskadeResponseDTO> lagreYrkesskade(YrkesskadeRequest request) {

        log.info("Sender yrkesskade melding: {}", request);
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new YrkesskadePostCommand(webClient, request, token.getTokenValue()).call())
                .doOnNext(response ->
                        log.info("Mottatt response fra yrkesskade service {}", response));
    }

    public Mono<SaksoversiktDTO> hentSaksoversikt(String ident) {

        log.info("Henter yrkeskade saksoversikt for ident {}", ident);
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new YrkesskadeGetCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(response ->
                        log.info("Mottatt saksoversikt fra yrkesskade service {}", response));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-yrkesskade-proxy";
    }
}
