package no.nav.pdl.forvalter.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.pdl.forvalter.config.credentials.PdlServiceProperties;
import no.nav.pdl.forvalter.consumer.command.PdlTestdataCommand;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public PdlTestdataConsumer(AccessTokenService accessTokenService, PdlServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public JsonNode sendPdlAdresseSoek(String url, String ident, Object body) {
        var accessToken = accessTokenService.generateToken(properties);
        return new PdlTestdataCommand(webClient, url, ident, body, accessToken.getTokenValue()).call();
    }
}
