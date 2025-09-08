package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.String.join;

@RequiredArgsConstructor
@Slf4j
public class PdlDataHentCommand implements Callable<Flux<FullPersonDTO>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer";

    private final WebClient webClient;
    private final List<String> identer;
    private final Integer sidenummer;
    private final Integer sidestorrelse;
    private final String token;

    public Flux<FullPersonDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_PERSONER_URL)
                        .queryParam("identer", identer)
                        .queryParam("sidenummer", sidenummer)
                        .queryParam("pagesize", sidestorrelse)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(FullPersonDTO.class)
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on GET of idents %s".formatted(join(",", identer))))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}
