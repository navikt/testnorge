package no.nav.registre.testnorge.avhengighetsanalysefrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.avhengighetsanalysefrontend.config.DependenciesProperties;
import no.nav.registre.testnorge.avhengighetsanalysefrontend.consumer.command.GetDependenciesCommand;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@Slf4j
@Component
public class DependencyConsumer {

    private final Set<String> urls;

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    public DependencyConsumer(RestTemplateBuilder restTemplateBuilder, DependenciesProperties properties) {
        this.restTemplate = restTemplateBuilder.build();
        this.urls = properties.getUrls();
        this.executorService = Executors.newFixedThreadPool(properties.getThreads());
    }


    private CompletableFuture<ApplicationDependenciesDTO> fetchDependencies(String url) {
        return CompletableFuture.supplyAsync(
                () -> new GetDependenciesCommand(url, restTemplate).call(),
                executorService
        );
    }


    public Set<ApplicationDependenciesDTO> fetchDependencies() {
        var futures = urls
                .stream()
                .map(this::fetchDependencies)
                .collect(Collectors.toList());

        log.info("Venter på {} avheninghetesanalyser", futures.size());
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.warn("Klarer ikke å hente alle avheningheter.", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
