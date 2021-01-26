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
            AccessTokenService accessTokenService,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        this.accessTokenService = accessTokenService;
        this.clientId = properties.getClientId();
        WebClient.Builder builder = WebClient.builder().baseUrl(properties.getUrl());

        if (proxyHost != null) {
            log.info("Setter opp proxy host {}", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        this.webClient = builder.build();
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