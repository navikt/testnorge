package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.HelsepersonellListe;
import no.nav.registre.testnorge.synt.sykemelding.exception.LegerNotFoundException;

@Slf4j
@Component
@DependencyOn("testnorge-helsepersonell-api")
public class HelsepersonellConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public HelsepersonellConsumer(RestTemplateBuilder restTemplateBuilder, @Value("${consumers.helsepersonell.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/helsepersonell";
    }

    @SneakyThrows
    public HelsepersonellListe hentHelsepersonell() {
        log.info("Henter helsepersonell...");
        var dto = restTemplate.exchange(RequestEntity.get(new URI(url)).build(), HelsepersonellListeDTO.class).getBody();

        if (dto == null) {
            throw new LegerNotFoundException("Klarer ikke å hente helsepersonell fra helsepersonell-api");
        }
        log.info("{} helsepersonell hentet", dto.getHelsepersonell().size());
        return new HelsepersonellListe(dto);
    }
}