package no.nav.dolly.budpro.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/failure")
@RequiredArgsConstructor
public class EmulatedFailureController {

    private final WebClient webClient;

    @GetMapping("/get")
    DummyDTO generateRequestedFailure(
            @RequestParam(value = "httpStatus", required = false, defaultValue = "500") int httpStatus,
            @RequestParam(value = "delayInMillis", required = false, defaultValue = "0") long delayInMillis) {
        if (delayInMillis > 0) {
            try {
                Thread.sleep(delayInMillis);
            } catch (InterruptedException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delay for %d millis".formatted(delayInMillis));
            }
        }
        throw new ResponseStatusException(HttpStatus.valueOf(httpStatus), "This is a generated failure with error code %s and delay %d".formatted(httpStatus, delayInMillis));
    }

    @GetMapping("/selfcheck")
    Mono<DummyDTO> selfCheck() {
        return new SelfCheckCommand(webClient, "<token>").call();
    }

}
