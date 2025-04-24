package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyTagsDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TagsGetCommand implements Callable<Mono<DollyTagsDTO>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/hentBolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final ParameterizedTypeReference<Map<String, List<String>>> TYPE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Mono<DollyTagsDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(identer)
                .retrieve()
                .bodyToMono(TYPE)
                .map(resultat -> DollyTagsDTO
                        .builder()
                        .personerTags(resultat)
                        .build())
                .retryWhen(WebClientError.is5xxException());
    }
}
