package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.UpdateOrganisasjonBestillingCommand;
import no.nav.testnav.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private static final int TIMEOUT_SECONDS = 10;
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .tcpConfiguration(tcpClient -> tcpClient
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_SECONDS * 1000)
                                        .doOnConnected(connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_SECONDS))
                                                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_SECONDS))))))
                .build();
    }

    public Long save(String uuid) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        log.info("Registrerer jobb med uuid: {}.", uuid);
        var id = new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
        log.info("Jobb registert med id {} og uuid: {}.", uuid, id);
        return id;
    }

    public void update(String uuid, String miljo, Long jobId, Long id) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).run();
    }
}
