package no.nav.pdl.forvalter.consumer;

import lombok.SneakyThrows;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.PersonExistsGetCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class PersonServiceConsumer {

    private static final long DELAY = 100; // milliseconds
    private static final int MAX_REPETIONS = 100;
    private WebClient webClient;
    private ServerProperties serverProperties;
    private TokenExchange tokenExchange;

    public PersonServiceConsumer(Consumers consumers, TokenExchange tokenExchange) {

        this.serverProperties = consumers.getPersonService();
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    @SneakyThrows
    public Mono<Boolean> existsIdent(String ident) {

        int repetions = 0;
        do {
            repetions++;
            Thread.sleep(DELAY);
            t
        } while (repetions < MAX_REPETIONS)
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new PersonExistsGetCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(result ->)
    }

    private Mono<Boolean> getPersonService(String ident, long counter, Boolean response) {

        if (isTrue(response) || counter == MAX_REPETIONS) {
            return Mono.just(response);

        } else {
            return Mono.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> personServiceConsumer.isPerson(ident.getKey(), ident.getValue())
                            .flatMapMany(resultat -> getPersonService(tidSlutt, LocalTime.now(), resultat, ident)));
        }

    }
}