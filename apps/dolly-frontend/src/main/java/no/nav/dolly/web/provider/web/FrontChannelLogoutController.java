package no.nav.dolly.web.provider.web;

import io.valkey.Jedis;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
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
import java.util.Optional;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("idporten")
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
                .filter(session -> Optional
                        .ofNullable(session.getAttribute("SPRING_SECURITY_CONTEXT"))
                        .map(SecurityContextImpl.class::cast)
                        .map(SecurityContextImpl::getAuthentication)
                        .map(Authentication::getPrincipal)
                        .filter(DefaultOidcUser.class::isInstance)
                        .map(DefaultOidcUser.class::cast)
                        .map(DefaultOidcUser::getIdToken)
                        .map(token -> token.<String>getClaim("sid"))
                        .map(claim -> claim.equals(sid))
                        .orElse(false))
                .flatMap(WebSession::invalidate)
                .then();

    }

    private List<String> getAllSessionIds() {
        return jedis
                .keys("*")
                .stream()
                .map(session -> session.split(":")[session.split(":").length - 1])
                .toList();
    }

}