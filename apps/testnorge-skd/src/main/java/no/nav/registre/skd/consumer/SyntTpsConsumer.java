package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.command.GetSyntSkdMeldingerCommand;
import no.nav.registre.skd.consumer.credential.SyntTpsGcpProperties;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@Slf4j
public class SyntTpsConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SyntTpsConsumer(
            SyntTpsGcpProperties syntTpsGcpProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = syntTpsGcpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntTpsGcpProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(
            String endringskode,
            Integer antallMeldinger
    ) {
        var response = tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetSyntSkdMeldingerCommand(endringskode, antallMeldinger, accessToken.getTokenValue(), webClient).call())
                .block();

        if (response != null && response.size() != antallMeldinger) {
            log.warn("Feil antall meldinger mottatt fra TPS-Syntetisereren. Forventet {}, men mottok {} meldinger.", antallMeldinger, response.size());
        }

        return response;
    }
}