package no.nav.dolly.budpro.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/failure")
@RequiredArgsConstructor
class EmulatedFailureController {

    private final WebClient webClient;
    private final EmulatedFailureService service;

    @GetMapping("/get")
    Mono<DummyDTO> generateRequestedFailure(
            @RequestParam(value = "httpStatus", required = false, defaultValue = "500") int httpStatus,
            @RequestParam(value = "delayInMillis", required = false, defaultValue = "0") long delayInMillis) {
        return service.emulateException(delayInMillis, httpStatus);
    }

    @GetMapping("/selfcheck")
    Mono<DummyDTO> selfCheck() {
        return new SelfCheckCommand(webClient).call();
    }

}
