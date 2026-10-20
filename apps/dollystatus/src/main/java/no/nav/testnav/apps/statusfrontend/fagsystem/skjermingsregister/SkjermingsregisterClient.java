package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SkjermingsregisterClient {

    Mono<SkjermingsregisterResourceStatus> getScreening(
            SkjermingsregisterRequest expectedRequest,
            LocalDateTime referenceTime
    );

    Mono<Void> createScreening(SkjermingsregisterRequest request);

    Mono<Void> updateScreening(SkjermingsregisterRequest request);
}
