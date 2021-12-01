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
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private static final int TIMEOUT_SECONDS = 10;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties properties,
            TokenExchange tokenExchange
    ) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
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
        var accessToken = tokenExchange.generateToken(properties).block();
        log.info("Registrerer jobb med uuid: {}.", uuid);
        var id = new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
        log.info("Jobb registert med id {} og uuid: {}.", uuid, id);
        return id;
    }

    public void update(String uuid, String miljo, Long jobId, Long id) {
        var accessToken = tokenExchange.generateToken(properties).block();
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).run();
    }
}
