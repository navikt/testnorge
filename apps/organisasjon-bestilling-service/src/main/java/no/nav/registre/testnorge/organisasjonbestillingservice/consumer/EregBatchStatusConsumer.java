package no.nav.registre.testnorge.organisasjonbestillingservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonbestillingservice.consumer.command.GetEregBatchStatusCommand;
import no.nav.registre.testnorge.organisasjonbestillingservice.domain.Order;

@Component
public class EregBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public EregBatchStatusConsumer(
            @Value("${consumers.ereg-batch-status-service.url}") String url,
            @Value("${consumers.ereg-batch-status-service.client-id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public Long getStatusKode(Order order) {
        var accessToken = accessTokenService.generateToken(clientId);
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }
}
