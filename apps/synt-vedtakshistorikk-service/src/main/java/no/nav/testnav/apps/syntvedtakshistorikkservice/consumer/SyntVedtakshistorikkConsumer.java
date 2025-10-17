package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.config.Consumers;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt.HentVedtakshistorikkCommand;
import no.nav.testnav.libs.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class SyntVedtakshistorikkConsumer {

    // Krav fra Arena et at tidligste dato(minimumsdato) for innsending av vedtakshistorikk er 01.10.2010.
    // Har satt datoen nå til 01.01.2015 siden Arena mangler støtte for tiltakspenger bak i tid.
    private static final LocalDate MINIMUM_DATE = LocalDate.of(2015, 1, 1);

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final WebClient webClient;
    private final Random rand = new Random();

    public SyntVedtakshistorikkConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getSyntVedtakshistorikk();
        this.tokenExchange = tokenExchange;
        var httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
        this.webClient = webClient
                .mutate()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<Vedtakshistorikk> syntetiserVedtakshistorikk(int antallIdenter) {
        List<String> oppstartsdatoer = new ArrayList<>(antallIdenter);

        for (var i = 0; i < antallIdenter; i++) {
            var dato = LocalDate.now().minusMonths(rand.nextInt(Math.toIntExact(ChronoUnit.MONTHS.between(MINIMUM_DATE, LocalDate.now()))));
            oppstartsdatoer.add(dato.toString());
        }
        try {
            return tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new HentVedtakshistorikkCommand(webClient, oppstartsdatoer, accessToken.getTokenValue()).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente vedtakshistorikk.", e);
            return Collections.emptyList();
        }

    }
}
