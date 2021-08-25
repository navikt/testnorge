package no.nav.organisasjonforvalter.consumer;

import no.nav.organisasjonforvalter.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingCommand;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class OrganisasjonBestillingConsumer {

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final OrganisasjonBestillingServiceProperties serviceProperties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
    }

    public Flux<BestillingStatus> getBestillingStatus(Status status) {

        return Flux.from(accessTokenService.generateToken(serviceProperties)
                .flatMap(token -> new OrganisasjonBestillingCommand(webClient, status,
                        token.getTokenValue()).call()));
    }

    public BestillingStatus getBestillingStatus(String uuid) {

        return accessTokenService.generateToken(serviceProperties)
                .flatMap(token -> new OrganisasjonBestillingCommand(webClient, Status.builder()
                        .uuid(uuid)
                        .build(),
                        token.getTokenValue()).call())
                .block();
    }
}
