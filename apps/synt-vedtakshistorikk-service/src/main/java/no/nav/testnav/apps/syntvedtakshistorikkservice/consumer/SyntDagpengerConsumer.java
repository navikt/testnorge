package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt.HentDagpengevedtakCommand;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Collections;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class SyntDagpengerConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public SyntDagpengerConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getSyntDagpenger();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public DagpengevedtakDTO syntetiserDagpengevedtak(Dagpengerettighet rettighet, LocalDate startdato) {
        var request = Collections.singletonList(startdato.toString());

        try {
            var response = tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new HentDagpengevedtakCommand(webClient, request, rettighet, accessToken.getTokenValue()).call())
                    .block();
            if (nonNull(response) && !response.isEmpty()) {
                return response.getFirst();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Klarte ikke hente syntetisk dagpengevedtak.", e);
            return null;
        }

    }
}
