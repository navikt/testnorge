package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;

@Slf4j
@DependencyOn("testnorge-statistikk-api")
@RequiredArgsConstructor
public class GetStatistikkCommand implements Callable<StatistikkDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final StatistikkType type;

    @Override
    public StatistikkDTO call() {

        var request = RequestEntity.get(URI.create(this.url + "/api/v1/statistikk/" + type.name())).build();

        var response = restTemplate.exchange(request, StatistikkDTO.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut " + type.name());
        }
        return response.getBody();
    }
}
