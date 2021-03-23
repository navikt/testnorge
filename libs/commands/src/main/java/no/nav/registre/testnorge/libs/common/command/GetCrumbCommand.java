package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;


@Slf4j
@RequiredArgsConstructor
public class GetCrumbCommand implements Callable<JenkinsCrumb> {
    private final WebClient webClient;
    private final String token;

    @Override
    public JenkinsCrumb call() {
        return webClient
                .get()
                .uri("/crumbIssuer/api/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JenkinsCrumb.class)
                .block();
    }
}
