package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetHelsepersonellCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.synt.sykemelding.domain.HelsepersonellListe;

@Slf4j
@Component
@DependencyOn("testnorge-helsepersonell-api")
public class HelsepersonellConsumer {
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final String clientId;

    public HelsepersonellConsumer(
            AccessTokenService accessTokenService,
            @Value("${consumers.helsepersonell.client_id}") String clientId,
            @Value("${consumers.helsepersonell.url}") String url
    ) {
        this.accessTokenService = accessTokenService;
        this.clientId = clientId;
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    @SneakyThrows
    public HelsepersonellListe hentHelsepersonell() {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        log.info("Henter helsepersonell...");
        HelsepersonellListeDTO dto = new GetHelsepersonellCommand(webClient, accessToken.getTokenValue()).call();
        log.info("{} helsepersonell hentet", dto.getHelsepersonell().size());
        return new HelsepersonellListe(dto);
    }
}