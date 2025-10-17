package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetCrumbCommand implements Callable<JenkinsCrumb> {
    private final WebClient webClient;
    private final String token;

    @Override
    public JenkinsCrumb call() {
        return webClient
                .get()
                .uri("/crumbIssuer/api/json")
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(JenkinsCrumb.class)
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
