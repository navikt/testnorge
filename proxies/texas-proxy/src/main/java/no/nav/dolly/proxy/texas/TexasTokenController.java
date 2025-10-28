package no.nav.dolly.proxy.texas;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasToken;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("token")
@Profile("!test")
@RequiredArgsConstructor
class TexasTokenController {

    private final Texas texas;

    @GetMapping("get")
    Mono<TexasToken> get(String audience) {
        return texas.get(audience);
    }

    @PostMapping("introspect")
    Mono<String> introspect(String token) {
        return texas.introspect(token);
    }

    @PostMapping("token")
    Mono<TexasToken> exchange(String audience, String token) {
        return texas.exchange(audience, token);
    }

}
