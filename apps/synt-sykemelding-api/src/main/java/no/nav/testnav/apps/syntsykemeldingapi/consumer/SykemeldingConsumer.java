package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;

@Slf4j
@Component
public class SykemeldingConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public SykemeldingConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.sykemelding.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    public ResponseEntity<String> opprettSykemelding(Sykemelding sykemelding) {
        return new PostSykemeldingCommand(restTemplate, url, sykemelding).call();
    }
}
