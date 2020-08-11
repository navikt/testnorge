package no.nav.registre.testnorge.synt.arbeidsforhold.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.command.GetKodeverkCommand;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.KodeverkSet;

@Slf4j
@Component
public class KodeverkConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final String applicationName;

    public KodeverkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${application.name}") String applicationName,
            @Value("${consumers.kodeverk.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.applicationName = applicationName;
    }

    public KodeverkSet getYrkerKodeverk() {
        var command = new GetKodeverkCommand(restTemplate, url, "Yrker", LocalDate.now(), applicationName);
        return new KodeverkSet(command.call());
    }
}
