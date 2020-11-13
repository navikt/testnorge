package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.PostSykemeldingCommand;
import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;

@Slf4j
@Component
@DependencyOn("testnorge-sykemelding-api")
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
