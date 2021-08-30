package no.nav.registre.skd.consumer.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Slf4j
public class GetSyntSkdMeldingerCommand implements Callable<List<RsMeldingstype>> {

    private final String endringskode;
    private final Integer antallMeldinger;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public GetSyntSkdMeldingerCommand(String endringskode, Integer antallMeldinger, WebClient webClient) {
        this.endringskode = endringskode;
        this.antallMeldinger = antallMeldinger;
        this.webClient = webClient;
    }

    @Override
    public List<RsMeldingstype> call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/generate/tps/{endringskode}")
                                    .queryParam("numToGenerate", antallMeldinger)
                                    .build(endringskode)
                    )
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente syntetiske SKD-meldinger.", e);
            return Collections.emptyList();
        }
    }

}
