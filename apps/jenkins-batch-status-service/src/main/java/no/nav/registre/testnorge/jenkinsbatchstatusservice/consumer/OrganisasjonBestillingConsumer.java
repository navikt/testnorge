package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public OrganisasjonBestillingConsumer(
            @Value("${consumers.organisasjon-bestilling-service.url}") String url,
            @Value("${consumers.organisasjon-bestilling-service.client-id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long jobId) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new SaveOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid).run();
    }
}
