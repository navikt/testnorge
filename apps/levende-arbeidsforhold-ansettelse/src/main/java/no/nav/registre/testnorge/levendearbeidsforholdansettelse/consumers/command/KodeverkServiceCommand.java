package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class KodeverkServiceCommand implements Callable<Map<String, String>>{
    private final WebClient webClient;
    private final String token;
    private final String kodeverk;  ///api/v1/kodeverk/Yrker/koder
    @Override
    public Map<String, String> call()  {
        JsonNode node = webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/kodeverk")
                        .queryParam("kodeverk", kodeverk)
                        .build()
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.convertValue(node.get("kodeverk"), new TypeReference<Map<String, String>>() {
            });
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
