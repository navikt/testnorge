package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.hendelse.v1.HendelseDTO;
import no.nav.testnav.libs.dto.hendelse.v1.HendelseType;

@Slf4j
@RequiredArgsConstructor
public class GetHendelseCommand implements Callable<List<HendelseDTO>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final HendelseType type;
    private final LocalDate date;

    @Override
    public List<HendelseDTO> call() {
        RequestEntity<Void> request = RequestEntity
                .get(URI.create(this.url + "?type=" + type + "&between=" + format(date)))
                .build();
        ResponseEntity<HendelseDTO[]> entity = restTemplate.exchange(request, HendelseDTO[].class);

        if (!entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut " + type.name());
        }

        return Arrays.asList(entity.getBody());
    }

    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
