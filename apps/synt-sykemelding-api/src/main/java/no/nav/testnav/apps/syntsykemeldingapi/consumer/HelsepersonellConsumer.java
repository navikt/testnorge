package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.HelsepersonellServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.domain.HelsepersonellListe;
import no.nav.testnav.libs.commands.GetHelsepersonellCommand;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class HelsepersonellConsumer {
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final HelsepersonellServiceProperties serviceProperties;

    public HelsepersonellConsumer(
            TokenExchange tokenExchange,
            HelsepersonellServiceProperties serviceProperties
    ) {
        this.tokenExchange = tokenExchange;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public HelsepersonellListe hentHelsepersonell() {
        var accessToken = tokenExchange.exchange(serviceProperties).block();
        log.info("Henter helsepersonell...");
        HelsepersonellListeDTO dto = new GetHelsepersonellCommand(webClient, accessToken.getTokenValue()).call();
        log.info("{} helsepersonell hentet", dto.getHelsepersonell().size());
        return new HelsepersonellListe(dto);
    }
}