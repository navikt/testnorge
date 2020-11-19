package no.nav.registre.testnorge.organisasjonmottakservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.organisasjonmottakservice.consumer.request.JenkinsCrumb;

@RequiredArgsConstructor
public class GetCrumbCommand implements Callable<JenkinsCrumb> {
    private final WebClient webClient;

    @Override
    public JenkinsCrumb call() {
        return webClient
                .get()
                .uri("/crumbIssuer/api/json")
                .retrieve()
                .bodyToMono(JenkinsCrumb.class)
                .block();
    }
}
