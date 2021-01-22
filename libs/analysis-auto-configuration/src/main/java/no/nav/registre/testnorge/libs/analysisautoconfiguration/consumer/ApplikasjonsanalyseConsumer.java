package no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceGCPProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.command.SaveAnalyseCommand;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class ApplikasjonsanalyseConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties serverProperties;

    public ApplikasjonsanalyseConsumer(
            ApplikasjonsanalyseServiceProperties serverProperties,
            AccessTokenService accessTokenService
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void save(ApplicationInfoDTO dto) {
        AccessToken accessToken = accessTokenService.generateClientCredentialAccessToken(serverProperties);
        new SaveAnalyseCommand(webClient, accessToken.getTokenValue(), dto).run();
    }
}
