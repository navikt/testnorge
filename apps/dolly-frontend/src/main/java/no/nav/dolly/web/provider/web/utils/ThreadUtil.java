package no.nav.dolly.web.provider.web.utils;

import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static ThreadUtil instance;
    private final Executor executor;

    private ThreadUtil() {
        this.executor = Executors.newFixedThreadPool(10);
    }

    public static ThreadUtil Instance() {
        if (instance == null) {
            instance = new ThreadUtil();
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
