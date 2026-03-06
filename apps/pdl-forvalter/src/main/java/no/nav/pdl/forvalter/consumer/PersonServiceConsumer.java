package no.nav.pdl.forvalter.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.PersonExistsGetCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class PersonServiceConsumer {

    private static final long DELAY = 100; // milliseconds
    private static final int MAX_REPETITIONS = 100;
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.serverProperties = consumers.getPersonService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    @SneakyThrows
    public Flux<Boolean> syncIdent(String ident) {

        return getPersonService(ident, new AtomicLong(0), false);
    }

    private Flux<Boolean> getPersonService(String ident, AtomicLong counter, Boolean response) {

        if (isTrue(response) || counter.get() == MAX_REPETITIONS) {
            log.info("Synkronisering av ident {} tok {} repetisjoner", ident, counter.get());
            return Flux.just(response);

        } else {
            counter.incrementAndGet();
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(DELAY))
                    .flatMap(delayed -> tokenExchange.exchange(serverProperties)
                            .flatMapMany(token -> new PersonExistsGetCommand(webClient, ident, token.getTokenValue()).call())
                            .flatMap(resultat -> getPersonService(ident, counter, resultat)));
        }
    }
}