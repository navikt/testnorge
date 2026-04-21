package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.UpdateOrganisasjonBestillingCommand;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<Long> save(String uuid) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> {
                    log.info("Registrerer jobb med uuid: {}.", uuid);
                    return new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
                })
                .doOnNext(id -> log.info("Jobb registert med id {} og uuid: {}.", id, uuid));
    }

    public Mono<Void> update(String uuid, String miljo, Long jobId, Long id) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> {
                    log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
                    var dto = new OrderDTO(miljo, jobId);
                    return new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).call();
                });
    }
}