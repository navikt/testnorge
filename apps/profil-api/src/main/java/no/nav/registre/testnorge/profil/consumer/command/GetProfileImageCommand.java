package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;

@Slf4j
@DependencyOn(value = "azure-ad", external = true)
@RequiredArgsConstructor
public class GetProfileImageCommand implements Callable<ByteArrayResource> {

    private final WebClient webClient;
    private final String accessToken;

    @Override
    public ByteArrayResource call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/v1.0/me/photo/120x120/$value").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();
    }
}
