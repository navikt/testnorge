package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetPoppMiljoerCommand implements Callable<Mono<Set<String>>> {

    public Mono<Set<String>> call() {

        return Mono.just(Set.of("q1", "q2"));
    }
}