package no.nav.testnav.apps.organisasjonbestillingservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.organisasjonbestillingservice.config.credentials.EregBatchStatusServiceProperties;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetEregBatchStatusCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class EregBatchStatusConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public EregBatchStatusConsumer(
            EregBatchStatusServiceProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Long getStatusKode(no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order order) {
        var accessToken = tokenExchange.generateToken(serviceProperties).block();
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }

    public Long getStatusKode(Order order) {
        var accessToken = tokenExchange.generateToken(serviceProperties).block();
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }

}
