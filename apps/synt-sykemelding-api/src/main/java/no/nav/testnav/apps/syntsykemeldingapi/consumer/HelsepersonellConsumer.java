package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.HelsepersonellServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetHelsepersonellCommand;
import no.nav.testnav.apps.syntsykemeldingapi.domain.HelsepersonellListe;
import no.nav.testnav.apps.syntsykemeldingapi.exception.HelsepersonellNotFoundException;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;


import static java.util.Objects.nonNull;

@Slf4j
@Component
public class HelsepersonellConsumer {
    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final HelsepersonellServiceProperties serviceProperties;

    public HelsepersonellConsumer(
            TokenExchange tokenExchange,
            HelsepersonellServiceProperties serviceProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @SneakyThrows
    public HelsepersonellListe hentHelsepersonell() {
        log.info("Henter helsepersonell...");
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetHelsepersonellCommand(webClient, accessToken.getTokenValue()).call())
                .block();

        if (nonNull(response)){
            log.info("{} helsepersonell hentet", response.getHelsepersonell().size());
            return new HelsepersonellListe(response);
        }else{
            log.warn("Feil oppsto i henting av helsepersonell");
            throw new HelsepersonellNotFoundException("Feil i henting av helsepersonell");
        }
    }
}