package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FrontChannelLogoutDevController {

    private final WebSessionManager webSessionManager;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        return Mono.fromRunnable(() -> {
            var manager = (DefaultWebSessionManager) webSessionManager;
            var store = (InMemoryWebSessionStore) manager.getSessionStore();

            store.getSessions().values().stream()
                    .filter(webSession -> sessionContainsSid(webSession, sid))
                    .toList()
                    .forEach(WebSession::invalidate);
        });
    }

    private Boolean sessionContainsSid(WebSession session, String sid) {
        if (sid != null) {
            var securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext != null) {
                var principal = (DefaultOidcUser) securityContext.getAuthentication().getPrincipal();
                return Objects.equals(sid, principal.getClaims().get("sid"));
            }
        }
        return false;
    }

}