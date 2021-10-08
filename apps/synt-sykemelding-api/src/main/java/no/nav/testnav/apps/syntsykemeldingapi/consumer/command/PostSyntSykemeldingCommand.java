package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class PostSyntSykemeldingCommand implements Callable<HashMap<String, SyntSykemeldingHistorikkDTO>> {

    private final Map<String, String> request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<HashMap<String, SyntSykemeldingHistorikkDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<Map<String, String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public HashMap<String, SyntSykemeldingHistorikkDTO> call() {
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/generate_sykmeldings_history_json").build()
                    )
                    .header("Authorization", "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(request), REQUEST_TYPE))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente data fra synthdata-elsam.", e);
            return null;
        }

    }
}
