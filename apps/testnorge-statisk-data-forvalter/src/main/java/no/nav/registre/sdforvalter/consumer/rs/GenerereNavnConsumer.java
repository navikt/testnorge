package no.nav.registre.sdforvalter.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.sdforvalter.consumer.rs.commnad.GenererNavnCommand;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class GenerereNavnConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public GenerereNavnConsumer(
            @Value("${consumers.genererenavnservice.client_id}") String clientId,
            @Value("${consumers.genererenavnservice.url}") String url,
            AccessTokenService accessTokenService) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }


    public String genererNavn() {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        NavnDTO navn = new GenererNavnCommand(webClient, accessToken.getTokenValue()).call();
        return navn.getAdjektiv() + " " + navn.getSubstantiv();
    }

}
