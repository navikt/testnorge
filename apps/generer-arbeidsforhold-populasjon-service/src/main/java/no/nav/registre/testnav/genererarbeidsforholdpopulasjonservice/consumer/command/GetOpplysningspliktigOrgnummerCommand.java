package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetOpplysningspliktigOrgnummerCommand implements Callable<Mono<Set<String>>> {
    private final WebClient webClient;
    private final String accessToken;
    private final String miljo;

    @Override
    public Mono<Set<String>> call() {
        log.info("Henter alle opplysningspliktige orgnummer i {}...", miljo);
        return webClient
                .get()
                .uri("/api/v1/opplysningspliktig")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });


    }
}
