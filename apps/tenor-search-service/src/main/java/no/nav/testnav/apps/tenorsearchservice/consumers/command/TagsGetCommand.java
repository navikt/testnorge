package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TagsGetCommand implements Callable<Mono<DollyTagsDTO>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/bolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String PERSONIDENTER = "Nav-Personidenter";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Mono<DollyTagsDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(PERSONIDENTER, String.join(",", identer))
                .retrieve()
                .bodyToMono(Map.class)
                .map(resultat ->
                        DollyTagsDTO.builder()
                                .personerTags(resultat)
                                .build())
                .retryWhen(WebClientError.is5xxException());
    }

}
