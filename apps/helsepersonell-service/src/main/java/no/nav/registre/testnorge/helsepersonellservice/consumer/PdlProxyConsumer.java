package no.nav.registre.testnorge.helsepersonellservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.PdlProxyProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetPdlBolkCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.PdlPersonBolk;
import no.nav.registre.testnorge.helsepersonellservice.util.FilLaster;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.registre.testnorge.helsepersonellservice.util.ExhangeStrategyUtil.biggerMemorySizeExchangeStrategy;

@Slf4j
@Component
public class PdlProxyConsumer {
    private static final String BOLK_PERSON_QUERY = "pdlperson/pdlbolkquery.graphql";
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public PdlProxyConsumer(
            PdlProxyProperties pdlProxyProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = pdlProxyProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(biggerMemorySizeExchangeStrategy())
                .baseUrl(pdlProxyProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public PdlPersonBolk getPdlPersoner(List<String> identer) {
        if (isNull(identer) || identer.isEmpty()) {
            return null;
        }

        var query = getBolkQueryFromFile();
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetPdlBolkCommand(identer, query, accessToken.getTokenValue(), webClient).call())
                .block();
    }

    private static String getBolkQueryFromFile() {
        try (var reader = new BufferedReader(new InputStreamReader(FilLaster.instans().lastRessurs(BOLK_PERSON_QUERY), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", BOLK_PERSON_QUERY, e);
            return null;
        }
    }

}
