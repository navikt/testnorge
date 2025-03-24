package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBEREG007LogCommand implements Callable<String> {

    private final WebClient webClient;
    private final String token;
    private final Long jobNumber;

    @Override
    public String call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("view/All/job/Start_BEREG007/{jobNumber}/logText/progressiveText")
                        .queryParam("start", 0)
                        .build(jobNumber)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
