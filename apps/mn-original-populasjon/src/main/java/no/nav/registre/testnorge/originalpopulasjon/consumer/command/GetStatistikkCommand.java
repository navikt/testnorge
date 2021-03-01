package no.nav.registre.testnorge.originalpopulasjon.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;

@RequiredArgsConstructor
public class GetStatistikkCommand implements Callable<StatistikkDTO> {
    private final WebClient webClient;
    private final StatistikkType type;
    private final String token;

    @Override
    public StatistikkDTO call() {
        return webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/statistikk/{type}")
                        .build(type)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(StatistikkDTO.class)
                .block();
    }
}
