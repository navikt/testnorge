package no.nav.registre.testnorge.organisasjonmottakservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.organisasjonmottakservice.consumer.request.JenkinsCrumb;

@Slf4j
@RequiredArgsConstructor
public class GetCrumbCommand implements Callable<JenkinsCrumb> {
    private final WebClient webClient;

    @Override
    public JenkinsCrumb call() {
        log.info("Het crumb issuer fra jenkins.");
        return webClient
                .get()
                .uri("/crumbIssuer/api/json")
                .retrieve()
                .bodyToMono(JenkinsCrumb.class)
                .block();
    }
}
