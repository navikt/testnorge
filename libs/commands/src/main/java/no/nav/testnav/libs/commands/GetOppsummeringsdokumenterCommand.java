package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetOppsummeringsdokumenterCommand implements Callable<List<OppsummeringsdokumentDTO>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String miljo;

    @SneakyThrows
    @Override
    public List<OppsummeringsdokumentDTO> call() {
        log.info("Henter alle oppsummeringsdokumenter i {}...", miljo);
        try {
            OppsummeringsdokumentDTO[] array = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/oppsummeringsdokumenter")
                            .build()
                    )
                    .header("miljo", this.miljo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(OppsummeringsdokumentDTO[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
            var list = Arrays.stream(array).collect(Collectors.toList());
            log.info("Fant {} dokumenter i {}.", list.size(), miljo);
            return list;
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}