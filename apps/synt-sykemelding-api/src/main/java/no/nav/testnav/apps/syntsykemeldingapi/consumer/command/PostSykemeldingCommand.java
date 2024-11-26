package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldningResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntsykemeldingapi.util.Headers.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PostSykemeldingCommand implements Callable<Mono<SykemeldningResponseDTO>> {

    private final WebClient webClient;
    private final String token;
    private final SykemeldingDTO sykemelding;

    @SneakyThrows
    @Override
    public Mono<SykemeldningResponseDTO> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/sykemeldinger").build()
                )
                .header(AUTHORIZATION, "Bearer " + token)
                .bodyValue(sykemelding)
                .retrieve()
                .bodyToMono(SykemeldningResponseDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Feil oppsto i innsending av sykemelding")));
    }
}
