package no.nav.adresse.service.consumer;

import no.nav.adresse.service.config.credentials.PdlServiceProperties;
import no.nav.adresse.service.consumer.command.MatrikkelAdresseSoekCommand;
import no.nav.adresse.service.consumer.command.VegAdresseSoekCommand;
import no.nav.adresse.service.dto.GraphQLRequest;
import no.nav.adresse.service.dto.MatrikkelAdresseResponse;
import no.nav.adresse.service.dto.VegAdresseResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PdlAdresseConsumer {

    public static final String TEMA = "Tema";

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

    public VegAdresseResponse sendVegadresseSoek(GraphQLRequest adresseQuery) {

        return accessTokenService.generateToken(properties)
                .flatMap(token -> new VegAdresseSoekCommand(webClient, adresseQuery, token.getTokenValue()).call())
                .block();
    }

    public MatrikkelAdresseResponse sendMatrikkeladresseSoek(GraphQLRequest adresseQuery) {

        return accessTokenService.generateToken(properties)
                .flatMap(token -> new MatrikkelAdresseSoekCommand(webClient, adresseQuery, token.getTokenValue()).call())
                .block();
    }

    public enum TemaGrunnlag {GEN, PEN}
}
