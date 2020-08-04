package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;

@Slf4j
@Component
@DependencyOn("hendelse-api")
public class HendelseConsumer {

    private final RestTemplate restTemplate;
    private final String url;

    public HendelseConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.hendelse.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/hendelser";
    }

    public List<HendelseDTO> getSykemeldingerAt(LocalDate date) {
        RequestEntity<Void> request = RequestEntity
                .get(URI.create(this.url + "?type=" + HendelseType.SYKEMELDING_OPPRETTET + "&between=" + format(date)))
                .build();

        log.info("Henter aktive opprette sykemeldinger.");
        ResponseEntity<HendelseDTO[]> entity = restTemplate.exchange(request, HendelseDTO[].class);

        if (!entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut antall sykemeldinger.");
        }

        log.info("Antall aktive sykemeldinger: {}", entity.getBody().length);
        return Arrays.asList(entity.getBody());
    }

    public List<HendelseDTO> getArbeidsforholdAt(LocalDate date) {
        RequestEntity<Void> request = RequestEntity
                .get(URI.create(this.url + "?type=" + HendelseType.ARBEIDSFORHOLD_OPPRETTET + "&between=" + format(date)))
                .build();

        log.info("Henter aktive arbeidsforhold.");
        ResponseEntity<HendelseDTO[]> entity = restTemplate.exchange(request, HendelseDTO[].class);

        if (!entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut aktive arbeidsforhold.");
        }

        log.info("Antall aktive arbeidsforhold: {}", entity.getBody().length);
        return Arrays.asList(entity.getBody());
    }


    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
