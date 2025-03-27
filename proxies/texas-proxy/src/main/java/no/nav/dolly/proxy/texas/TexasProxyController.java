package no.nav.dolly.proxy.texas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasException;
import no.nav.dolly.libs.texas.TexasToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
class TexasProxyController {

    private final Texas texas;

    @PostMapping("/api/v1/token")
    Mono<TexasToken> token(@RequestBody Request request) {
        log.info("Request for token from identity provider {} for target {}", request.identity_provider(), request.target());
        return texas.get(request.target());
    }

    private record Request(
            String identity_provider,
            String target
    ) {
    }

}
