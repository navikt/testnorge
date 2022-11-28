package no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials.AaregSyntServiceProperties;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.command.SaveOpplysningspliktigCommand;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.webjars.NotFoundException;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class AaregSyntConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public AaregSyntConsumer(TokenExchange tokenExchange,
                             AaregSyntServiceProperties properties,
                             ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
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
        var token = tokenExchange.exchange(properties).block();
        if (isNull(token)) {
            throw new NotFoundException("Klarte ikke å generere AccessToken for AaregSyntService");
        }
        return "Bearer " + token.getTokenValue();
    }
}
