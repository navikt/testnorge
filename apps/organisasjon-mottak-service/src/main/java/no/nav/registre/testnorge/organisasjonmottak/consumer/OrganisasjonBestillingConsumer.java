package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonmottak.config.Consumers;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterBestillingCommand;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class OrganisasjonBestillingConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public OrganisasjonBestillingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getOrganisasjonBestillingService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
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

            var order = tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new RegisterBestillingCommand(webClient, accessToken.getTokenValue(), orderDTO).call())
                    .block();
            log.info("Ordre med id:{} opprettet.", order.getId());
        } catch (Exception ex) {
            log.error("Noe gikk galt med innsending til organisasjon-bestilling-service.", ex);
        }
    }
}