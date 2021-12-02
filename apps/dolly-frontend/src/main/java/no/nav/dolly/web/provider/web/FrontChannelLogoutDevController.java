package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.repository.OidcReactiveMapSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FrontChannelLogoutDevController {

    private final WebSessionManager webSessionManager;
    private final ReactiveSessionRepository repository;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        var manager = (DefaultWebSessionManager) webSessionManager;
        var store = manager.getSessionStore();
        var repo = (OidcReactiveMapSessionRepository) repository;

        return repo.getSessionsWithSid(sid)
                .flatMapMany(Flux::fromIterable)
                .flatMap(store::retrieveSession)
                .flatMap(WebSession::invalidate)
                .then();
    }

}