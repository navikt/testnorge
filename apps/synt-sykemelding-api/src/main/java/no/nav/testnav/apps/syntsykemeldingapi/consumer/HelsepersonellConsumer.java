package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.commands.GetHelsepersonellCommand;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.HelsepersonellServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.domain.HelsepersonellListe;

@Slf4j
@Component
public class HelsepersonellConsumer {
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final HelsepersonellServiceProperties serviceProperties;

    public HelsepersonellConsumer(
            AccessTokenService accessTokenService,
            HelsepersonellServiceProperties serviceProperties
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @SneakyThrows
    public HelsepersonellListe hentHelsepersonell() {
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties).block();
        log.info("Henter helsepersonell...");
        HelsepersonellListeDTO dto = new GetHelsepersonellCommand(webClient, accessToken.getTokenValue()).call();
        log.info("{} helsepersonell hentet", dto.getHelsepersonell().size());
        return new HelsepersonellListe(dto);
    }
}