package no.nav.registre.inntekt.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostSyntInntektCommand implements Callable<SortedMap<String, List<RsInntekt>>> {

    private final Map<String, List<RsInntekt>> fnrInntektMap;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<SortedMap<String, List<RsInntekt>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public SortedMap<String, List<RsInntekt>> call() {
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/generate/inntekt")
                            .build())
                    .body(BodyInserters.fromValue(fnrInntektMap))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (HttpStatusCodeException e) {
            log.warn(e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Uventet feil fra syntetisering", e);
            return null;
        }
    }
}
