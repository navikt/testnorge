package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.SaveOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.UpdateOrganisasjonBestillingCommand;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public Long save(String uuid) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        log.info("Registrerer jobb med uuid: {}.", uuid);
        return new SaveOrganisasjonBestillingCommand(webClient, accessToken.getTokenValue(), uuid).call();
    }

    public void update(String uuid, String miljo, Long jobId, Long id) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        log.info("Oppretter organisasjon bestilling for uuid {} med job id: {}", uuid, jobId);
        var dto = new OrderDTO(miljo, jobId);
        new UpdateOrganisasjonBestillingCommand(webClient, dto, accessToken.getTokenValue(), uuid, id).run();
    }
}
