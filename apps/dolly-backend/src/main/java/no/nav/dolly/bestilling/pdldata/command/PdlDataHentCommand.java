package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.String.join;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(FullPersonDTO.class)
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on GET of idents %s".formatted(join(",", identer))))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}
