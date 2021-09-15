package no.nav.testnav.apps.apptilganganalyseservice.util;

import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TreadUtil {
    private static TreadUtil instance;
    private final Executor executor;

    private TreadUtil() {
        this.executor = Executors.newFixedThreadPool(10);
    }

    public static TreadUtil Instance() {
        if (instance == null) {
            instance = new TreadUtil();
        }
        return instance;
    }

    @SneakyThrows
    public <T> T resolve(Mono<T> mono) {
        return CompletableFuture.supplyAsync(mono::block, executor).get();
    }

    public <T> List<T> resolve(Flux<T> flux) {
        return resolve(flux.collectList());
    }

}
