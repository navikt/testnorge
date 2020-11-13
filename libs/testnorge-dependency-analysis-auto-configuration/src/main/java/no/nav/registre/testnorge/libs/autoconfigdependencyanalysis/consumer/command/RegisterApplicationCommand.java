package no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class RegisterApplicationCommand implements Runnable {
    private final WebClient webClient;
    private final String applicationName;
    private final String accessToken;

    @Override
    public void run() {
        webClient
                .put()
                .uri(builder -> builder.path("/api/v1/applications/{name}").build(applicationName))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
