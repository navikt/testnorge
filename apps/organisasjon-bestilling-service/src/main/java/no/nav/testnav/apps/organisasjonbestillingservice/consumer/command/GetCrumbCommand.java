package no.nav.testnav.apps.organisasjonbestillingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;


@Slf4j
@RequiredArgsConstructor
public class GetCrumbCommand implements Callable<Mono<JenkinsCrumb>> {
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<JenkinsCrumb> call() {
        return webClient
                .get()
                .uri("/crumbIssuer/api/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JenkinsCrumb.class);
    }
}
