package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentTagsCommand implements Callable<Mono<TagsDTO>> {
    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/bolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String PERSONIDENTER = "Nav-Personidenter";

    private final WebClient webClient;
    private final String token;
    private final List<String> identer;

    @Override
    public Mono<TagsDTO> call() {

        return webClient.get().uri(builder -> builder.path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(PERSONIDENTER, String.join(",", identer))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resultat -> TagsDTO.builder()
                        .personerTags(resultat)
                        .build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
