package no.nav.registre.sdforvalter.consumer.rs.tp;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.credential.TestnorgeTpProperties;
import no.nav.registre.sdforvalter.consumer.rs.hodejegeren.command.GetLevendeIdenterCommand;
import no.nav.registre.sdforvalter.consumer.rs.tp.command.OpprettPersonerTpCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Objects.isNull;


@Slf4j
@Component
public class TpConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public TpConsumer(
            @Value("${consumers.testnorge-tp.url}") String testnorgeTpUrl,
            TokenExchange tokenExchange,
            TestnorgeTpProperties serviceProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient
                .builder()
                .baseUrl(testnorgeTpUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    /**
     * @param data        Fnr som skal legges til i tp
     * @param environment Miljøet de skal legges til i
     * @return true hvis den ble lagret i tp, false hvis de ikke ble lagret
     */
    public boolean send(List<String> data, String environment) {
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new OpprettPersonerTpCommand(webClient, data, environment, accessToken.getTokenValue()).call())
                .block();

        if (isNull(response)){
            log.warn("Noe skjedde med initialisering av TP i gitt miljø. Det kan være at databasen ikke er koblet opp til miljø {}", environment);
            return false;
        }
        return true;
    }
}
