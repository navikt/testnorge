package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials.GenererNavnServiceProperties;
import no.nav.registre.testnorge.libs.common.command.generernavnservice.v1.GenererNavnCommand;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class GenererNavnConsumer {

    private final WebClient webClient;
    private final GenererNavnServiceProperties properties;
    private final AccessTokenService accessTokenService;

    public GenererNavnConsumer(GenererNavnServiceProperties properties, AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public NavnDTO genereNavn() {
        var accessToken = accessTokenService.generateToken(properties).block();
        GenererNavnCommand command = new GenererNavnCommand(webClient, accessToken.getTokenValue(), 1);
        return command.call()[0];
    }
}
