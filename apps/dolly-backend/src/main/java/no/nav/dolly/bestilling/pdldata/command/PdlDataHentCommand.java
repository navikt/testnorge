package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataHentCommand implements Callable<Mono<FullPersonDTO[]>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer";
    private static final String IDENTER = "identer";
    private static final String PAGE_NO = "sidenummer";
    private static final String PAGE_SIZE = "pagesize";

    private final WebClient webClient;
    private final List<String> identer;
    private final Integer sidenummer;
    private final Integer sidestorrelse;
    private final String token;

    public Mono<FullPersonDTO[]> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_PERSONER_URL)
                        .queryParam(IDENTER, identer)
                        .queryParam(PAGE_NO, sidenummer)
                        .queryParam(PAGE_SIZE, sidestorrelse)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(FullPersonDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
