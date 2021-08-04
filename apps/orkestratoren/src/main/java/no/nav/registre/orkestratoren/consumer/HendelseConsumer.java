package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.command.GetHendelseCommand;
import no.nav.testnav.libs.dto.hendelse.v1.HendelseDTO;
import no.nav.testnav.libs.dto.hendelse.v1.HendelseType;

@Slf4j
@Component
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
        log.info("Henter aktive opprette sykemeldinger.");
        List<HendelseDTO> list = new GetHendelseCommand(restTemplate, url, HendelseType.SYKEMELDING_OPPRETTET, date).call();
        log.info("Antall aktive sykemeldinger: {}", list.size());
        return list;
    }

    public List<HendelseDTO> getArbeidsforholdAt(LocalDate date) {
        log.info("Henter aktive arbeidsforhold.");
        List<HendelseDTO> list = new GetHendelseCommand(restTemplate, url, HendelseType.ARBEIDSFORHOLD_OPPRETTET, date).call();
        log.info("Antall aktive arbeidsforhold: {}", list.size());
        return list;
    }
}
