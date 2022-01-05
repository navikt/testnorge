package no.nav.registre.syntrest.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSyntMeldekortCommand implements Callable<List<String>> {

    private final int antall;
    private final String meldegruppe;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/meldekort/{meldegruppe}/{antall}")
                                .build(meldegruppe, antall)
                )
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}