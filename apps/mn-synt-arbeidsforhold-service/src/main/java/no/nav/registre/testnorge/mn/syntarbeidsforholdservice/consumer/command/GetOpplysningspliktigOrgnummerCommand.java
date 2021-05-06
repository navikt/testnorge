package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetOpplysningspliktigOrgnummerCommand implements Callable<Set<String>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String miljo;

    @Override
    public Set<String> call() {
        log.info("Henter alle opplysningspliktige orgnummer i {}...", miljo);
        var response = webClient
                .get()
                .uri("/api/v1/opplysningspliktig")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .retrieve()
                .bodyToMono(String[].class)
                .block();
        log.info("Fant {} opplysningspliktige orgnummer i {}.", response.length, miljo);
        return Arrays.stream(response).collect(Collectors.toSet());

    }
}
