package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.OrgForvalterServiceProperties;
import no.nav.pdl.forvalter.consumer.command.OrganisasjonForvalterCommand;
import no.nav.pdl.forvalter.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class OrganisasjonForvalterConsumer {

    private static final String IMPORT_ORG_URL = "/api/v2/organisasjoner/framiljoe";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public OrganisasjonForvalterConsumer(TokenExchange tokenExchange,
                                         OrgForvalterServiceProperties properties) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjonsForvalter_getOrganisasjon"})
    public Map<String, Map<String, Object>> getOrganisasjon(String orgNummer) {

        return tokenExchange.exchange(properties).flatMap(
                        token -> new OrganisasjonForvalterCommand(webClient, IMPORT_ORG_URL,
                                String.format("orgnummer=%s", orgNummer), token.getTokenValue()).call())
                .block();
    }
}
