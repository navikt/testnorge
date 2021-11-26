package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataHentCommand implements Callable<Mono<FullPersonDTO[]>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer";
    private static final String IDENTER = "identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<FullPersonDTO[]> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_PERSONER_URL)
                        .queryParam(IDENTER, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(FullPersonDTO[].class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
