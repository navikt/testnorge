package no.nav.testnav.kodeverkservice.consumer;

import no.nav.testnav.kodeverkservice.config.Consumers;
import no.nav.testnav.kodeverkservice.consumer.command.KodeverkGetCommand;
import no.nav.testnav.kodeverkservice.dto.KodeverkBetydningerResponse;
import no.nav.testnav.kodeverkservice.utility.KommunerUtility;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.testnav.kodeverkservice.utility.KommunerUtility.KOMMUNER;
import static no.nav.testnav.kodeverkservice.utility.KommunerUtility.KOMMUNER2024;

@Service
public class KodeverkConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public KodeverkConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder
    ) {
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
                        !KOMMUNER2024.equals(kodeverk) ? kodeverk : KOMMUNER,
                        token.getTokenValue()).call())
                .map(response -> !KOMMUNER2024.equals(kodeverk) ? response :
                        KommunerUtility.filterKommuner2024(response));
    }
}
