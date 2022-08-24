package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.SykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.exception.LagreSykemeldingException;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class SykemeldingConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SykemeldingConsumer(
            SykemeldingProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void opprettSykemelding(SykemeldingDTO sykemelding) {
        var response = tokenExchange.exchange(serviceProperties).flatMap(accessToken ->
                        new PostSykemeldingCommand(webClient, accessToken.getTokenValue(), sykemelding).call())
                .block();

        if (isNull(response) || !HttpStatus.OK.equals(response.getStatusCode())) {
            throw new LagreSykemeldingException("Feil oppsto i innsending av sykemelding");
        }
    }
}
