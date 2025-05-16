package no.nav.dolly.budpro.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class EmulatedFailureController {

    private final WebClient webClient;
    private final EmulatedFailureService service;

    @GetMapping("/get")
    Mono<DummyDTO> generateRequestedFailure(
            @RequestParam(value = "httpStatus", required = false, defaultValue = "500") int httpStatus,
            @RequestParam(value = "delayInMillis", required = false, defaultValue = "0") long delayInMillis) {

        return service
                .emulateException(delayInMillis, httpStatus)
                .doOnError(EmulatedFailureException.class, e -> log.error("Caught a planned exception", e));

    }

    @GetMapping("/selfcheck")
    Mono<DummyDTO> selfCheck() {
        return new SelfCheckCommand(webClient).call();
    }

}
