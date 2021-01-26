package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.UpdateOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties properties,
            AccessTokenService accessTokenService,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        this.clientId = properties.getClientId();
        this.accessTokenService = accessTokenService;

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

    public Long save(String uuid) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        log.info("Registrerer jobb med uuid: {}.", uuid);
        return new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
    }

    public void update(String uuid, String miljo, Long jobId, Long id) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).run();
    }
}
