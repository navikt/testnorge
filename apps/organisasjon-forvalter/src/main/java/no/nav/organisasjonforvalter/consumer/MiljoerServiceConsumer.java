package no.nav.organisasjonforvalter.consumer;

import static java.util.Collections.emptySet;
import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_MILJOER;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.organisasjonforvalter.config.credentials.MiljoerServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.MiljoerServiceCommand;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class MiljoerServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final MiljoerServiceProperties serviceProperties;

    public MiljoerServiceConsumer(
            MiljoerServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    @Cacheable(CACHE_MILJOER)
    public Set<String> getOrgMiljoer() {

        try {
            return Stream.of(tokenExchange.generateToken(serviceProperties)
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
