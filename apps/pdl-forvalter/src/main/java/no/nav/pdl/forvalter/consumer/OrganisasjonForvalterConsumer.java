package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.OrgForvalterServiceProperties;
import no.nav.pdl.forvalter.consumer.command.OrganisasjonForvalterCommand;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class OrganisasjonForvalterConsumer {

    private static final String IMPORT_ORG_URL = "/api/v1/organisasjon/import";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public OrganisasjonForvalterConsumer(AccessTokenService accessTokenService,
                                         OrgForvalterServiceProperties properties) {

        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Map<String, Map<String, String>> get(String orgNummer) {

        var accessToken = accessTokenService.generateToken(properties);
        return new OrganisasjonForvalterCommand(webClient, IMPORT_ORG_URL,
                String.format("orgnummer=%s", orgNummer), accessToken.getTokenValue()).call();
    }
}
