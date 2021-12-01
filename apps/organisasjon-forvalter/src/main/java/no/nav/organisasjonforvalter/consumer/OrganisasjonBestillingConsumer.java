package no.nav.organisasjonforvalter.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import no.nav.organisasjonforvalter.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingCommand;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingIdsCommand;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingStatusCommand;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Service
public class OrganisasjonBestillingConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final OrganisasjonBestillingServiceProperties serviceProperties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<BestillingStatus> getBestillingStatus(Status status) {

        return Flux.from(tokenExchange.exchange(serviceProperties)
                .flatMap(token -> new OrganisasjonBestillingStatusCommand(webClient, status,
                        token.getTokenValue()).call()));
    }

    public Flux<Status> getBestillingId(Status status) {

        return Flux.from(tokenExchange.exchange(serviceProperties)
                .flatMap(token -> new OrganisasjonBestillingIdsCommand(webClient, status,
                        token.getTokenValue()).call()));
    }

    public BestillingStatus getBestillingStatus(String uuid) {

        return tokenExchange.exchange(serviceProperties)
                .flatMap(token -> new OrganisasjonBestillingCommand(webClient, Status.builder()
                        .uuid(uuid)
                        .build(),
                        token.getTokenValue()).call())
                .block();
    }
}
