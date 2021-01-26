package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.JenkinsServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.registre.testnorge.libs.common.command.GetCrumbCommand;
import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class JenkinsConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public JenkinsConsumer(
            JenkinsServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.clientId = properties.getClientId();
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    private JenkinsCrumb getCrumb() {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        return new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
    }

    public Long getJobNumber(Long itemId) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        var dto = new GetQueueItemCommand(webClient, accessToken.getTokenValue(), getCrumb(), itemId).call();
        return dto.getNumber();
    }

    public String getJobLog(Long jobNumber) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        return new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), jobNumber).call();
    }
}