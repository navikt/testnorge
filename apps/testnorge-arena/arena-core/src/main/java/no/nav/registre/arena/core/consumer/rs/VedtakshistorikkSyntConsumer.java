package no.nav.registre.arena.core.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.command.HentVedtakshistorikkCommand;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@DependencyOn(value = "nais-synthdata-arena-vedtakshistorikk", external = true)
public class VedtakshistorikkSyntConsumer {

    private static final LocalDate MINIMUM_DATE = LocalDate.of(2010, 10, 1); // krav i arena
    // mangler støtte for tiltakspenger bak i tid
    private static final LocalDate MIDLERTIDIG_MINIMUM_DATE = LocalDate.of(2017, 6, 1);

    private final WebClient webClient;
    private Random rand;

    public VedtakshistorikkSyntConsumer(
            Random rand,
            @Value("${synt-arena-vedtakshistorikk.rest-api.url}") String arenaVedtakshistorikkServerUrl
    ) {
        this.rand = rand;
        this.webClient = WebClient.builder().baseUrl(arenaVedtakshistorikkServerUrl).build();
    }

    public List<Vedtakshistorikk> syntetiserVedtakshistorikk(int antallIdenter) {
        List<String> oppstartsdatoer = new ArrayList<>(antallIdenter);

        for (int i = 0; i < antallIdenter; i++) {
            var dato = LocalDate.now().minusMonths(rand.nextInt(Math.toIntExact(ChronoUnit.MONTHS.between(MIDLERTIDIG_MINIMUM_DATE, LocalDate.now()))));
            oppstartsdatoer.add(dato.toString());
        }

        return new HentVedtakshistorikkCommand(webClient, oppstartsdatoer).call();
    }
}
