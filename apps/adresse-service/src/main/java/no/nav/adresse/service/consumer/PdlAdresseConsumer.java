package no.nav.adresse.service.consumer;

import no.nav.adresse.service.config.credentials.PdlServiceProperties;
import no.nav.adresse.service.consumer.command.PdlAdresseSoekCommand;
import no.nav.adresse.service.dto.GraphQLRequest;
import no.nav.adresse.service.dto.PdlAdresseResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PdlAdresseConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public PdlAdresseConsumer(AccessTokenService accessTokenService, PdlServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public PdlAdresseResponse sendPdlAdresseSoek(GraphQLRequest adresseQuery) {
        var accessToken = accessTokenService.generateToken(properties);
        return new PdlAdresseSoekCommand(webClient, adresseQuery, accessToken.getTokenValue()).call();
    }
}
