package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetOrgnummerCommand implements Callable<Set<String>> {
    private final WebClient webClient;
    private final String accessToken;
    private final Integer antall;

    @Override
    public Set<String> call() {
        var response = webClient
                .get()
                .uri("/api/v1/orgnummer")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("antall", antall.toString())
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        return Arrays.stream(response).collect(Collectors.toSet());

    }
}
