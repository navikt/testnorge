package no.nav.registre.testnorge.personexportapi.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personexportapi.consumer.dto.EndringsmeldingDTO;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetTpsfMeldingerFromPageCommand implements Callable<List<EndringsmeldingDTO>> {
    private final WebClient webClient;
    private final String username;
    private final String password;
    private final String avspillingsgruppe;
    private final int pageNumber;

    @Override
    public List<EndringsmeldingDTO> call() {
        var array = webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/endringsmelding/skd/gruppe/meldinger/{avspillingsgruppe}/{pageNumber}")
                        .build(avspillingsgruppe, pageNumber)
                )
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .bodyToMono(EndringsmeldingDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
        return Arrays.stream(array).collect(Collectors.toList());
    }
}
