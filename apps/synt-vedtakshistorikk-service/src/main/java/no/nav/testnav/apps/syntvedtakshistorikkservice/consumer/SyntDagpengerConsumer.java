package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt.HentDagpengevedtakCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.SyntDagpengerProperties;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Random;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class SyntDagpengerConsumer {
    private static final LocalDate MINIMUM_DATE = LocalDate.of(2015, 1, 1);

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;
    private final Random rand = new Random();

    public SyntDagpengerConsumer(
            SyntDagpengerProperties properties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(properties.getUrl())
                .build();
    }

    public DagpengevedtakDTO syntetiserDagpengevedtak(Dagpengerettighet rettighet) {
        //TODO sjekk hvilket dato-intervall som skal brukes for dagpenger
        var dato = LocalDate.now().minusMonths(rand.nextInt(Math.toIntExact(ChronoUnit.MONTHS.between(MINIMUM_DATE, LocalDate.now()))));
        var request = Collections.singletonList(dato.toString());

        try {
            var response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new HentDagpengevedtakCommand(webClient, request, rettighet, accessToken.getTokenValue()).call())
                    .block();
            if (nonNull(response) && !response.isEmpty()) {
                return response.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Klarte ikke hente syntetisk dagpengevedtak.", e);
            return null;
        }

    }
}
