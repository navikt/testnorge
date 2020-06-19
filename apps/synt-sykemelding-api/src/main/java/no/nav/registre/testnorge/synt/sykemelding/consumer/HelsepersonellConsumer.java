package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.LegeListe;

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

    public LegeListe hentLeger() {
        log.info("Henter leger...");
        var dto = restTemplate.getForObject(url, LegeListeDTO.class);
        if (dto == null) {
            throw new RuntimeException("Klarer ikke å hente leger fra helsepersonell-api");
        }
        log.info("{} leger hentet", dto.getLeger().size());
        return new LegeListe(dto);
    }
}