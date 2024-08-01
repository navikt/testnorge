package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentTagsCommand implements Callable<TagsDTO> {

    private final WebClient webClient;
    private final String token;
    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String PERSONIDENTER = "Nav-Personidenter";


    private final List<String> identer;
    @Override
    public TagsDTO call() {
        return webClient.get().uri(builder -> builder.path(PDL_TESTDATA).path(PDL_TAGS_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(PERSONIDENTER , String.join(",", identer))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resultat ->
                        TagsDTO.builder()
                                .personerTags(resultat).build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)
                        ).filter(WebClientFilter::is5xxException))
                .doOnError(error -> log.error("JsonNode error {}", error.getLocalizedMessage()))
                .block();

    }
}
