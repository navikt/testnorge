package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt.HentVedtakshistorikkCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.SyntVedtakshistorikkProperties;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import reactor.core.publisher.Mono;

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
    private final ServerProperties serviceProperties;
    private final WebClient webClient;
    private final Random rand = new Random();

    public SyntVedtakshistorikkConsumer(
            SyntVedtakshistorikkProperties syntVedtakshistorikkProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = syntVedtakshistorikkProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(syntVedtakshistorikkProperties.getUrl())
                .build();
    }

    public List<Vedtakshistorikk> syntetiserVedtakshistorikk(int antallIdenter) {
        List<String> oppstartsdatoer = new ArrayList<>(antallIdenter);

        for (var i = 0; i < antallIdenter; i++) {
            var dato = LocalDate.now().minusMonths(rand.nextInt(Math.toIntExact(ChronoUnit.MONTHS.between(MINIMUM_DATE, LocalDate.now()))));
            oppstartsdatoer.add(dato.toString());
        }
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new HentVedtakshistorikkCommand(webClient, oppstartsdatoer, accessToken.getTokenValue()).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente vedtakshistorikk.", e);
            return Collections.emptyList();
        }

    }
}
