package no.nav.dolly.budpro.texas;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/texas")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TexasController {

    private final Texas texas;

    @GetMapping("/get")
    Mono<TexasToken> get(String consumerName) {
        return texas.getToken(consumerName);
    }

    @GetMapping("/exchange")
    Mono<TexasToken> exchange(String consumerName, String token) {
        return texas.exchangeToken(consumerName, token);
    }

    @GetMapping("/introspect")
    Mono<String> introspect(String token) {
        return texas.introspect(token);
    }

}
