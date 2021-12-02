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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.util.List;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("prod")
public class FrontChannelLogoutController {

    private final WebSessionManager webSessionManager;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid, Jedis jedis) {
        var sessionIds = getAllSessionIds(jedis);
        var manager = (DefaultWebSessionManager) this.webSessionManager;
        var sessions = Flux.concat(sessionIds
                .stream()
                .map(sessionId -> manager.getSessionStore().retrieveSession(sessionId))
                .toList());

        return sessions
                .filter(session -> session.getAttribute("SPRING_SECURITY_CONTEXT") != null)
                .filter(session -> {
                    var securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
                    if (securityContext.getAuthentication().getPrincipal() instanceof DefaultOidcUser user) {
                        return user.getIdToken().getClaim("sid").equals(sid);
                    }
                    return false;
                })
                .flatMap(WebSession::invalidate)
                .then();
    }

    private List<String> getAllSessionIds(Jedis jedis) {
        return jedis.keys("*").stream().map(session -> session.split(":")[session.split(":").length - 1]).toList();
    }
}
