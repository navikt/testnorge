package no.nav.dolly.budpro.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/failure")
@RequiredArgsConstructor
@Slf4j
class EmulatedFailureController {

    private final WebClient webClient;
    private final EmulatedFailureService service;
    private final Texas texas;

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

    @GetMapping("/token/get")
    Mono<TexasToken> getToken(
            @RequestParam String audience
    ) {
        return texas.get(audience);
    }

    @PostMapping("/token/introspect")
    Mono<String> introspectToken(
            @RequestBody String token
    ) {
        return texas.introspect(token);
    }

    @PostMapping("/token/exchange")
    Mono<TexasToken> exchangeToken(
            @RequestParam String audience,
            @RequestBody String token) {
        return texas.exchange(audience, token);
    }

}
