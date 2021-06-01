package no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials.AaregSyntServiceProperties;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.command.SaveOpplysningspliktigCommand;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;

@Slf4j
@Component
public class AaregSyntConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public AaregSyntConsumer(AccessTokenService accessTokenService, AaregSyntServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void saveOpplysningspliktig(Oppsummeringsdokument oppsummeringsdokument, String miljo) {
        var accessToken = accessTokenService.generateToken(properties);
        new SaveOpplysningspliktigCommand(
                webClient,
                oppsummeringsdokument.toXml(),
                accessToken.getTokenValue(),
                miljo.equals("t4") ? "u5" : miljo //TODO fix redirect from t4 to u5
        ).run();
    }
}
