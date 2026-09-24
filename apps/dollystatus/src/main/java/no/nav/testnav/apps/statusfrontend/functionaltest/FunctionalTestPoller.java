package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class FunctionalTestPoller {

    private FunctionalTestPoller() {
    }

    public static <T> Mono<T> pollUntil(
            Supplier<Mono<T>> request,
            Predicate<T> expectedResult,
            Duration pollInterval,
            Duration pollTimeout,
            Scheduler scheduler
    ) {
        return Mono.defer(request)
                .repeatWhen(completed -> completed.delayElements(pollInterval, scheduler))
                .filter(expectedResult)
                .next()
                .timeout(
                        pollTimeout,
                        Mono.error(new FunctionalTestVerificationTimeoutException()),
                        scheduler);
    }
}
