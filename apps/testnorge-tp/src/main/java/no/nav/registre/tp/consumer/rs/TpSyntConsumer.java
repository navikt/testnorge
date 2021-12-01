package no.nav.registre.tp.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.tp.consumer.rs.command.GetSyntTpYtelserCommand;
import no.nav.registre.tp.consumer.rs.credential.SyntTpProperties;
import no.nav.registre.tp.database.models.TYtelse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class TpSyntConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public TpSyntConsumer(
            SyntTpProperties syntTpProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntTpProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntTpProperties.getUrl())
                .build();
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "synt"})
    public List<TYtelse> getSyntYtelser(
            int numToGenerate
    ) {
        var token = tokenExchange.generateToken(serviceProperties).block().getTokenValue();
        return new GetSyntTpYtelserCommand(numToGenerate, token, webClient).call();
    }
}
