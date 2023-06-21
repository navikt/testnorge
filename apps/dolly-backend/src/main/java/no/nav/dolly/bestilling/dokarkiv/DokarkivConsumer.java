package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivGetMiljoeCommand;
import no.nav.dolly.bestilling.dokarkiv.command.DokarkivPostCommand;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.credentials.DokarkivProxyServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class DokarkivConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public DokarkivConsumer(
            DokarkivProxyServiceProperties properties,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {
        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = webClientBuilder
                .baseUrl(properties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public Flux<DokarkivResponse> postDokarkiv(String environment, DokarkivRequest dokarkivRequest) {

        log.info("Sender dokarkiv melding for ident {} miljoe {} request {}",
                dokarkivRequest.getBruker().getId(), environment, dokarkivRequest);

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new DokarkivPostCommand(webClient, environment, dokarkivRequest,
                        token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv_getEnvironments" })
    public Mono<List<String>> getEnvironments() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new DokarkivGetMiljoeCommand(webClient, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dokarkiv-proxy";
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}
