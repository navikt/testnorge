package no.nav.testnav.kodeverkservice.consumer;

import no.nav.testnav.kodeverkservice.config.Consumers;
import no.nav.testnav.kodeverkservice.consumer.command.KodeverkGetCommand;
import no.nav.testnav.kodeverkservice.dto.KodeverkBetydningerResponse;
import no.nav.testnav.kodeverkservice.utility.FilterUtility;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KodeverkConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public KodeverkConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        this.tokenService = tokenService;
        serverProperties = consumers.getKodeverkApi();
        this.webClient = webClientBuilder
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(configurer -> configurer
                                        .defaultCodecs()
                                        .maxInMemorySize(32 * 1024 * 1024))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KodeverkGetCommand(webClient,
                        FilterUtility.hentKodeverk(kodeverk),
                        token.getTokenValue()).call())
                .map(FilterUtility::filtrerKodeverk);
    }
}
