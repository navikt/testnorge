package no.nav.dolly.consumer.dokumentarkiv;

import no.nav.dolly.bestilling.dokarkiv.command.DokarkivGetDokument;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
public class SafConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public SafConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient webClient) {

        serverProperties = consumers.getSafProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "saf_getDokument"})
    public Mono<DokarkivResponse> getDokument(String miljoe, String journalpostId, String dokumentInfoId, String variantFormat) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DokarkivGetDokument(webClient,
                        miljoe, journalpostId, dokumentInfoId, variantFormat, token.getTokenValue()).call());
    }
}
