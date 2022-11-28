package no.nav.registre.sdforvalter.consumer.rs.hodejegeren;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.credential.HodejegerenProperties;
import no.nav.registre.sdforvalter.consumer.rs.hodejegeren.command.GetAlleIdenterCommand;
import no.nav.registre.sdforvalter.consumer.rs.hodejegeren.command.GetLevendeIdenterCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class HodejegerenConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public HodejegerenConsumer(
            @Value("${consumers.testnorge-hodejegeren.url}") String hodejegerenServerUrl,
            HodejegerenProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;

        this.webClient = WebClient.builder()
                .baseUrl(hodejegerenServerUrl)
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    /**
     * @param playgroupId AvspillergruppeId som man ønsker å hente fnr fra
     * @return En liste med fnr som eksisterer i gruppen
     */
    public List<String> getPlaygroupFnrs(Long playgroupId) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetAlleIdenterCommand(playgroupId, webClient, accessToken.getTokenValue()).call())
                .block();
    }

    public List<String> getLivingFnrs(Long playgroupId, String environment) {
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetLevendeIdenterCommand(playgroupId, environment, webClient, accessToken.getTokenValue()).call())
                .block();
    }

}
