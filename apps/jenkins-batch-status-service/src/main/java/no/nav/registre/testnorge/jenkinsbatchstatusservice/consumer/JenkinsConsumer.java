package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.credentials.JenkinsServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.registre.testnorge.libs.common.command.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class JenkinsConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public JenkinsConsumer(
            JenkinsServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    private JenkinsCrumb getCrumb() {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        return new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
    }

    public Long getJobNumber(Long itemId) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        var dto = new GetQueueItemCommand(webClient, accessToken.getTokenValue(), getCrumb(), itemId).call();
        return dto.getNumber();
    }

    public String getJobLog(Long jobNumber) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        return new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), jobNumber).call();
    }
}