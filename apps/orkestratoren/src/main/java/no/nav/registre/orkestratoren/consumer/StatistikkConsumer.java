package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.orkestratoren.consumer.command.GetStatistikkCommand;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;

@Slf4j
@Component
@DependencyOn("testnorge-statistikk-api")
public class StatistikkConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public StatistikkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.statistikk.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    public double getAntallSykemeldtIProsent() {
        var command = new GetStatistikkCommand(restTemplate, url, StatistikkType.ANTALL_ARBEIDSTAKERE_SYKEMELDT);
        log.info("Henter antall arbeidstakere som i snitt er sykemeldt.");
        return command.call().getValue();
    }

    public double getAntallArbeidstakereSomErIArbeidsstyrkenIProsent() {
        var command = new GetStatistikkCommand(restTemplate, url, StatistikkType.ANTALL_ARBEIDSTAKERE_SOM_ER_I_ARBEIDSSTYRKEN);
        log.info("Henter antall personer som i snitt er i arbeide.");
        return command.call().getValue();
    }
}