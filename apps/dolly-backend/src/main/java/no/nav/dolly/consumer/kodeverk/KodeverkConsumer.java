package no.nav.dolly.consumer.kodeverk;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.kodeverk.command.KodeverkGetCommand;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class KodeverkConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public KodeverkConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavKodeverkService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Map<String, String>> getKodeverkByName(String kodeverk) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new KodeverkGetCommand(webClient, kodeverk, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet kodeverk {}, status {} melding {}",
                        response.getKodeverknavn(), response.getStatus(), response.getMessage()))
                .map(KodeverkDTO::getKodeverk);
    }
}
