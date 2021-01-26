package no.nav.registre.testnorge.organisasjonbestillingservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonbestillingservice.config.credentials.EregBatchStatusServiceProperties;
import no.nav.registre.testnorge.organisasjonbestillingservice.consumer.command.GetEregBatchStatusCommand;
import no.nav.registre.testnorge.organisasjonbestillingservice.domain.Order;

@Component
public class EregBatchStatusConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties serviceProperties;

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

    public Long getStatusKode(Order order) {
        var accessToken = accessTokenService.generateToken(serviceProperties);
        var command = new GetEregBatchStatusCommand(
                webClient,
                order.getBatchId(),
                accessToken.getTokenValue(),
                order.getMiljo()
        );
        return command.call();
    }
}
