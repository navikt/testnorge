package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.OrgForvalterServiceProperties;
import no.nav.pdl.forvalter.consumer.command.OrganisasjonForvalterCommand;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class OrganisasjonForvalterConsumer {

    private static final String IMPORT_ORG_URL = "/api/v2/organisasjoner/framiljoe";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;

    public OrganisasjonForvalterConsumer(AccessTokenService accessTokenService,
                                         OrgForvalterServiceProperties properties) {

        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Map<String, Map<String, Object>> get(String orgNummer) {

        return accessTokenService.generateToken(properties).flatMap(
                        token -> new OrganisasjonForvalterCommand(webClient, IMPORT_ORG_URL,
                                String.format("orgnummer=%s", orgNummer), token.getTokenValue()).call())
                .block();
    }
}
