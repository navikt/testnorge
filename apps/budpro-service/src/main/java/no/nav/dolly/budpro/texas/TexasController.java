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

    private final TexasService service;

    @GetMapping("/token")
    Mono<TexasToken> getToken(String consumerName) {
        return service.getToken(consumerName);
    }

}
