package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingIdsCommand;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingStatusCommand;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrganisasjonBestillingConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public OrganisasjonBestillingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getOrganisasjonBestillingService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<BestillingStatus> getBestillingStatus(Status status) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OrganisasjonBestillingStatusCommand(webClient, status,
                        token.getTokenValue()).call())
                .doOnNext(response ->
                        log.info("Mottatt response fra Organisasjon-Bestilling-Service: {}", response));
    }

    public Mono<Status> getBestillingId(Status status) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OrganisasjonBestillingIdsCommand(webClient, status,
                        token.getTokenValue()).call());
    }
}
