package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetMNOrganisasjonerCommand implements Callable<List<OrganisasjonDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String miljo;

    @SneakyThrows
    @Override
    public List<OrganisasjonDTO> call() {
        log.info("Henter alle Mini-Norge organisasjoner...");
        try {
            OrganisasjonDTO[] array = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/organisasjoner")
                            .build()
                    )
                    .header("miljo", miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OrganisasjonDTO[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
            var list = Arrays.asList(array);
            log.info("Fant {} Mini-Norge organisasjoner.", list.size());
            return list;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}