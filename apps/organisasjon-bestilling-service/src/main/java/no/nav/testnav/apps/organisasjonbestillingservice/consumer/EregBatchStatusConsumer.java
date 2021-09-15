package no.nav.testnav.apps.organisasjonbestillingservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetEregBatchStatusCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.apps.organisasjonbestillingservice.config.credentials.EregBatchStatusServiceProperties;

@Component
public class EregBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties serviceProperties;

    public EregBatchStatusConsumer(
            EregBatchStatusServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = serviceProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Long getStatusKode(no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order order) {
        var accessToken = accessTokenService.generateToken(serviceProperties).block();
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }

    public Long getStatusKode(Order order) {
        var accessToken = accessTokenService.generateToken(serviceProperties).block();
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }

}
