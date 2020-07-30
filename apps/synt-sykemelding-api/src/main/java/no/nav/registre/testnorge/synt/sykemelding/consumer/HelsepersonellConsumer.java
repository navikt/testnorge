package no.nav.registre.testnorge.synt.sykemelding.consumer;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.LegeListe;
import no.nav.registre.testnorge.synt.sykemelding.exception.LegerNotFoundException;

@Slf4j
@Component
@DependencyOn("helsepersonell-api")
public class HelsepersonellConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public HelsepersonellConsumer(RestTemplateBuilder restTemplateBuilder, @Value("${consumers.helsepersonell.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/helsepersonell/leger";
    }

    @SneakyThrows
    public LegeListe hentLeger() {
        log.info("Henter leger...");
        var dto = restTemplate.exchange(RequestEntity.get(new URI(url)).build(), LegeListeDTO.class).getBody();

        if (dto == null) {
            throw new LegerNotFoundException("Klarer ikke å hente leger fra helsepersonell-api");
        }
        log.info("{} leger hentet", dto.getLeger().size());
        return new LegeListe(dto);
    }
}