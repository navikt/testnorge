package no.nav.registre.testnorge.avhengighetsanalyseservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.avhengighetsanalyseservice.adapter.ApplicationAdapter;
import no.nav.registre.testnorge.avhengighetsanalyseservice.consumer.commnad.GetDependenciesCommand;
import no.nav.registre.testnorge.avhengighetsanalyseservice.domain.Application;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@Slf4j
@Component
public class DependencyConsumer {
    private static final int THREADS = 5;

    private final ApplicationAdapter applicationAdapter;
    private final Executor executor;

    public DependencyConsumer(ApplicationAdapter applicationAdapter) {
        this.applicationAdapter = applicationAdapter;
        this.executor = Executors.newFixedThreadPool(THREADS);
    }

    private CompletableFuture<ApplicationDependenciesDTO> fetchDependencies(Application application) {
        return CompletableFuture.supplyAsync(
                () -> new GetDependenciesCommand(application.getHost()).call(),
                executor
        );
    }

    public Set<ApplicationDependenciesDTO> getDependencies() {
        var futures = applicationAdapter
                .getAllApplications()
                .stream()
                .map(this::fetchDependencies)
                .collect(Collectors.toList());

        log.info("Venter på {} avheninghetesanalyser.", futures.size());
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
