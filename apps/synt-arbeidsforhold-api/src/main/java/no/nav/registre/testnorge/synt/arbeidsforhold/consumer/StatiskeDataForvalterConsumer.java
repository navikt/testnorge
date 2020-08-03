package no.nav.registre.testnorge.synt.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.statiskedataforvalter.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.domain.Organisasjon;

@Slf4j
@Component
public class StatiskeDataForvalterConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public StatiskeDataForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.statiske-data-forvalter.url}") String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/organisasjons?gruppe=DOLLY";
    }

    public Organisasjon getRandomOrganisasjonWhichSupportsArbeidsforhold() {
        RequestEntity<Void> build = RequestEntity
                .get(URI.create(url))
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<OrganisasjonListeDTO> response = restTemplate
                .exchange(build, OrganisasjonListeDTO.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Klarte ikke å hente Dolly organsiasjoner fra dolly");
        }
        List<OrganisasjonDTO> list = response.getBody().getListe().stream().filter(OrganisasjonDTO::isKanHaArbeidsforhold).collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new RuntimeException("Fant ingen orgnaisasjoner som kan ha arbeidsforhold");
        }

        OrganisasjonDTO randomOrg = list.get(
                ThreadLocalRandom.current().nextInt(0, list.size())
        );
        return new Organisasjon(randomOrg.getOrgnr());
    }
}
