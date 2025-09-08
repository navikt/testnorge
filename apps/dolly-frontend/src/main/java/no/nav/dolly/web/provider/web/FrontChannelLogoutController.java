package no.nav.dolly.web.provider.web;

import io.valkey.Jedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("idporten")
@Slf4j
public class FrontChannelLogoutController {

    private final WebSessionManager webSessionManager;
    private final Jedis jedis;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        var sessionIds = getAllSessionIds();
        var manager = (DefaultWebSessionManager) this.webSessionManager;
        var sessions = Flux.concat(sessionIds
                .stream()
                .map(sessionId -> manager.getSessionStore().retrieveSession(sessionId))
                .toList());

        return sessions
                .filter(session -> session.getAttribute("SPRING_SECURITY_CONTEXT") != null)
                .filter(session -> {
                    var securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
                    if (securityContext == null) {
                        log.warn("No security context found for session {}", session.getId());
                        return false;
                    }
                    if (securityContext.getAuthentication().getPrincipal() instanceof DefaultOidcUser user) {
                        var claim = user.getIdToken().getClaim("sid");
                        return claim != null && claim.equals(sid);
                    }
                    return false;
                })
                .flatMap(WebSession::invalidate)
                .then();
    }

    private List<String> getAllSessionIds() {
        var sessionIds = jedis
                .keys("*")
                .stream()
                .map(session -> session.split(":")[session.split(":").length - 1])
                .toList();
        log.info("Found {} session(s) to invalidate during logout", sessionIds.size());
        return sessionIds;
    }
}