package no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.command;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class SaveAnalyseCommand implements Runnable {

    private final WebClient webClient;
    private final String token;

    @Override
    public void run() {
        webClient
                .put()
                .uri(builder -> builder.path("/api/v1/applications/{name}").build("applicationName"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
