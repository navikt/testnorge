package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.OrganisasjonForvalterCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class OrganisasjonForvalterConsumer {

    private static final String IMPORT_ORG_URL = "/api/v2/organisasjoner/framiljoe";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public OrganisasjonForvalterConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getOrgForvalter();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Map<String, Map<String, Object>>> get(String orgNummer) {

        return tokenExchange.exchange(serverProperties).flatMap(
                token -> new OrganisasjonForvalterCommand(webClient, IMPORT_ORG_URL,
                        String.format("orgnummer=%s", orgNummer), token.getTokenValue()).call());
    }
}
