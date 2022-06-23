package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.SyntSykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSyntSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
public class SyntSykemeldingHistorikkConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntSykemeldingHistorikkConsumer(
            SyntSykemeldingProperties syntProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = syntProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @SneakyThrows
    public SyntSykemeldingHistorikkDTO genererSykemeldinger(String ident, LocalDate startDato) {
        log.info("Generererer sykemelding for {} fom {}", ident, startDato.toString());

        var request = Map.of(ident, startDato.toString());
        var accessToken = tokenExchange.exchange(serviceProperties).block().getTokenValue();

        var response = new PostSyntSykemeldingCommand(request, accessToken, webClient).call();

        if (response == null) {
            throw new GenererSykemeldingerException("Klarte ikke å generere sykemeldinger.");
        }
        log.info("Sykemelding generert.");
        return response.get(ident);
    }
}