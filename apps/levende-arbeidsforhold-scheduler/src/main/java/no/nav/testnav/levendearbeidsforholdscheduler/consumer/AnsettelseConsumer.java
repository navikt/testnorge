package no.nav.testnav.levendearbeidsforholdscheduler.consumer;

import no.nav.testnav.levendearbeidsforholdscheduler.config.Consumers;
import no.nav.testnav.levendearbeidsforholdscheduler.consumer.command.AnsettelsesCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AnsettelseConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AnsettelseConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.serverProperties = consumers.getLevendeArbeidsforholdAnsettelse();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<String> opprettAnsettelser() {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new AnsettelsesCommand(webClient, token.getTokenValue()).call());
    }
}
