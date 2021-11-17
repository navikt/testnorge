package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.repository.OidcReactiveMapSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FrontChannelLogoutDevController {

    private final OidcReactiveMapSessionRepository reactiveSessionRepository;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        return reactiveSessionRepository.deleteBySid(sid);
    }

}