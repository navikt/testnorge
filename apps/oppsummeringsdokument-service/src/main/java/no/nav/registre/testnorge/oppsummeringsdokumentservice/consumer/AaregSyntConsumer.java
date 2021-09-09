package no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials.AaregSyntServiceProperties;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.command.SaveOpplysningspliktigCommand;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;

@Slf4j
@Component
public class AaregSyntConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;

    public AaregSyntConsumer(AccessTokenService accessTokenService, AaregSyntServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void saveOpplysningspliktig(Oppsummeringsdokument oppsummeringsdokument, String miljo) {
        log.info("Oppsummeringsdokument med opplysningspliktig {} i {}.", oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer(), miljo);
        var accessToken = accessTokenService.generateToken(properties).block();
        new SaveOpplysningspliktigCommand(
                webClient,
                oppsummeringsdokument.toXml(),
                accessToken.getTokenValue(),
                miljo.equals("t4") ? "u5" : miljo //TODO fix redirect from t4 to u5
        ).run();
    }
}
