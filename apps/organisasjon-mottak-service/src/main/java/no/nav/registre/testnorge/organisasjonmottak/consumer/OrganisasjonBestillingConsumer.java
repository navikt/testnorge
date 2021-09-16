package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.organisasjonmottak.config.properties.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterBestillingCommand;
import no.nav.testnav.libs.dto.organiasjonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long queueId) {
        try {
            var orderDTO = OrderDTO
                    .builder()
                    .miljo(miljo)
                    .queueId(queueId)
                    .uuid(uuid)
                    .build();

            var order = accessTokenService.generateClientCredentialAccessToken(properties)
                    .flatMap(accessToken -> new RegisterBestillingCommand(webClient, accessToken.getTokenValue(), orderDTO).call())
                    .block();
            log.info("Ordre med {} opprettet.", order.getId());
        } catch (Exception ex) {
            log.error("Noe gikk galt med innsending til organisasjon-bestilling-service.", ex);
        }

    }


}
