package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.credentials.TpServiceProperties;
import no.nav.registre.sdforvalter.consumer.rs.command.OpprettPersonerTpCommand;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Objects.isNull;


@Slf4j
@Component
public class TpConsumer {

    private final WebClient webClient;
    private final TpServiceProperties serverProperties;
    private final TokenExchange tokenExchange;

    public TpConsumer(
            TpServiceProperties serverProperties,
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
     * @param data        Fnr som skal legges til i tp
     * @param environment Miljøet de skal legges til i
     * @return true hvis den ble lagret i tp, false hvis de ikke ble lagret
     */
    public boolean send(List<String> data, String environment) {
        var response = tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new OpprettPersonerTpCommand(webClient, accessToken.getTokenValue(), data, environment).call())
                .block();

        if (isNull(response)){
            log.warn("Noe skjedde med initialisering av TP i gitt miljø. Det kan være at databasen ikke er koblet opp til miljø {}", environment);
            return false;
        }
        return true;
    }
}
