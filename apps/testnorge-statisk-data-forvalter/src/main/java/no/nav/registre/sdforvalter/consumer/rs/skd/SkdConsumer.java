package no.nav.registre.sdforvalter.consumer.rs.skd;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.credentials.SkdServiceProperties;
import no.nav.registre.sdforvalter.consumer.rs.skd.command.SkdStartAvspillingCommand;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class SkdConsumer {

    private final WebClient webClient;
    private final SkdServiceProperties serverProperties;
    private final TokenExchange tokenExchange;

    public SkdConsumer(
            SkdServiceProperties serverProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    /**
     * @param playgroup   AvspillergruppeId som skal spilles av når denne funksjonen er invokert
     * @param environment Miljøet som gruppen skal spilles av til
     */
    public void send(Long playgroup, String environment) {
        tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new SkdStartAvspillingCommand(
                        webClient, accessToken.getTokenValue(), playgroup, environment).call())
                .subscribe(response -> {
                    if (isNull(response) || response.getAntallFeilet() != 0) {
                        log.warn("Fikk ikke opprettet alle identene i TPS, burde bli manuelt sjekket for overlapp. " +
                                "Kan også være mulig at man prøver å initialisere et miljø som er allerede initialisert");
                    }
                    if (nonNull(response)) {
                        response.getFailedStatus().forEach(s -> log.error("Status på feilende melding: {}", s));
                    }
                });
    }
}