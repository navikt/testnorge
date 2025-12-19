package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.kodeverk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class KodeverkServiceCommand implements Callable<Mono<Map<String, String>>> {

    private static final TypeReference<Map<String, String>> KODEVERK_TYPE = new TypeReference<>() {
    };

    private final WebClient webClient;
    private final String token;
    private final String kodeverk;
    /// api/v1/kodeverk/Yrker/koder
    private final ObjectMapper mapper;

    @Override
    public Mono<Map<String, String>> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/kodeverk")
                        .queryParam("kodeverk", kodeverk)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> nonNull(node) ?
                        mapper.convertValue(node.get("kodeverk"), KODEVERK_TYPE) :
                        HashMap.<String, String>newHashMap(0))
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.empty());
    }
}
