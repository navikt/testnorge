package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.UpdateOrganisasjonBestillingCommand;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public OrganisasjonBestillingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getOrganisasjonBestillingService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Long save(String uuid) {
        var accessToken = Objects.requireNonNull(tokenExchange.exchange(serverProperties).block(), "Token var null.");
        log.info("Registrerer jobb med uuid: {}.", uuid);
        var id = new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
        log.info("Jobb registert med id {} og uuid: {}.", uuid, id);
        return id;
    }

    public void update(String uuid, String miljo, Long jobId, Long id) {
        var accessToken = Objects.requireNonNull(tokenExchange.exchange(serverProperties).block(), "Token var null.");
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).run();
    }
}
