package no.nav.testnav.apps.generersyntmeldekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class GetSyntMeldekortCommand implements Callable<Mono<List<String>>> {

    private final int antall;
    private final String meldegruppe;
    private final Double arbeidstimer;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<String>> call() {
        return webClient.get()
                .uri(builder -> {
                            if (isNull(arbeidstimer)) {
                                return builder.path("/api/v1/meldekort/{meldegruppe}/{antall}")
                                        .build(meldegruppe, antall);
                            } else {
                                return builder.path("/api/v1/meldekort/{meldegruppe}/{antall}")
                                        .queryParam("arbeidstimer", arbeidstimer.toString())
                                        .build(meldegruppe, antall);
                            }
                        }
                )
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }
}
