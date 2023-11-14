package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.MiljoerServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_MILJOER;

@Slf4j
@Service
public class MiljoerServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public MiljoerServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {

        serverProperties = consumers.getTestnavMiljoerService();
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    @Cacheable(CACHE_MILJOER)
    public Set<String> getOrgMiljoer() {

        try {
            return Stream.of(tokenExchange.exchange(serverProperties)
                            .flatMap(token ->
                                    new MiljoerServiceCommand(webClient, token.getTokenValue()).call()).block())
                    .filter(env -> !env.equals("u5") && !env.equals("qx"))
                    .collect(Collectors.toSet());

        } catch (RuntimeException e) {
            log.error("Feilet å hente miljøer fra miljoer-service", e);
            return emptySet();
        }
    }
}
