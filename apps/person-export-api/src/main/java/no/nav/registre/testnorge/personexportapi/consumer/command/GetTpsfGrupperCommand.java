package no.nav.registre.testnorge.personexportapi.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personexportapi.consumer.dto.GruppeDTO;
import no.nav.registre.testnorge.personexportapi.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetTpsfGrupperCommand implements Callable<List<GruppeDTO>> {
    private final WebClient webClient;
    private final String username;
    private final String password;

    @Override
    public List<GruppeDTO> call() {
        GruppeDTO[] response = webClient
                .get()
                .uri("/api/v1/endringsmelding/skd/grupper")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .bodyToMono(GruppeDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
        return Arrays.stream(response).collect(Collectors.toList());
    }
}
