package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.kodeverk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class KodeverkServiceCommand implements Callable<Mono<Map<String, String>>> {

    private final WebClient webClient;
    private final String token;
    private final String kodeverk;  ///api/v1/kodeverk/Yrker/koder
    private final ObjectMapper mapper;

    @Override
    public Mono<Map<String, String>> call()  {

        return webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/kodeverk")
                        .queryParam("kodeverk", kodeverk)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> (Map<String, String>) (nonNull(node) ?
                        mapper.convertValue(node.get("kodeverk"), new TypeReference<>(){}) : Mono.empty()))
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.empty());
    }
}
