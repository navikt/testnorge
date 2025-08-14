package no.nav.dolly.budpro.test;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
class EmulatedFailureService {

    Mono<DummyDTO> emulateException(long delayInMillis, int httpStatus) {
        return Mono
                .delay(Duration.ofMillis(delayInMillis))
                .then(Mono.error(new EmulatedFailureException(HttpStatus.valueOf(httpStatus), "This is a generated failure with error code %s and delay %d".formatted(httpStatus, delayInMillis))));
    }

}
