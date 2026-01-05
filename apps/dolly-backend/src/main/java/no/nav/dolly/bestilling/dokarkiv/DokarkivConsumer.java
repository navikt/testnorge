package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivGetMiljoeCommand;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivPostCommand;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class DokarkivConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public DokarkivConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient webClient) {

        serverProperties = consumers.getTestnavDollyProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "dokarkiv-opprett"})
    public Mono<DokarkivResponse> postDokarkiv(String environment, DokarkivRequest dokarkivRequest) {

        log.info("Dokarkiv sender melding for ident {} miljoe {} request {}",
                dokarkivRequest.getBruker().getId(), environment, dokarkivRequest);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DokarkivPostCommand(webClient, environment, dokarkivRequest,
                        token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "dokarkiv_getEnvironments"})
    public Mono<List<String>> getEnvironments() {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DokarkivGetMiljoeCommand(webClient, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dolly-proxy";
    }

}
