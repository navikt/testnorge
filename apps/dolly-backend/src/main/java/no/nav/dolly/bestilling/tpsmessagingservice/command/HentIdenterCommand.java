package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class HentIdenterCommand implements Callable<List<TpsIdentStatusDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String IDENTER_URL = "/api/v1/identer";
    private static final String IDENTER_PARAM = "identer";

    private final WebClient webClient;
    private final List<String> miljoer;
    private final List<String> identer;
    private final String token;

    @Override
    public List<TpsIdentStatusDTO> call() {

        return List.of(webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(IDENTER_URL)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .queryParam(IDENTER_PARAM, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(TpsIdentStatusDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block());
    }
}
