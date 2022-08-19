package no.nav.testnav.apps.tpservice.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpservice.consumer.rs.command.synt.GetSyntTpYtelserCommand;
import no.nav.testnav.apps.tpservice.consumer.rs.credential.SyntTpProperties;
import no.nav.testnav.apps.tpservice.database.models.TYtelse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class TpSyntConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public TpSyntConsumer(
            SyntTpProperties syntTpProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = syntTpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntTpProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "synt"})
    public List<TYtelse> getSyntYtelser(
            int numToGenerate
    ) {
        try{
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new GetSyntTpYtelserCommand(
                            numToGenerate, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente meldinger fra synthdata-tp.", e);
            return new LinkedList<>();
        }
    }
}
