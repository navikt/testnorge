package no.nav.testnav.apps.oppsummeringsdokumentservice.consumer;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oppsummeringsdokumentservice.config.Consumers;
import no.nav.testnav.apps.oppsummeringsdokumentservice.consumer.command.SaveOpplysningspliktigCommand;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class AaregSyntConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public AaregSyntConsumer(
            TokenExchange tokenExchange,
            Consumers consumers) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getAaregSyntServices();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void saveOpplysningspliktig(Oppsummeringsdokument oppsummeringsdokument, String miljo) {
        log.info("Oppsummeringsdokument {} sendt til miljø: {}.", Json.pretty(oppsummeringsdokument), miljo);
        new SaveOpplysningspliktigCommand(
                webClient,
                oppsummeringsdokument.toXml(),
                getAccessToken(),
                miljo.equals("t4") ? "u5" : miljo //TODO fix redirect from t4 to u5
        ).run();
    }

    private String getAccessToken() {
        var token = tokenExchange.exchange(serverProperties).block();
        if (isNull(token)) {
            throw new IllegalArgumentException("Klarte ikke å generere AccessToken for AaregSyntService");
        }
        return "Bearer " + token.getTokenValue();
    }
}
