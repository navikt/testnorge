package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("idporten")
@Slf4j
class FrontChannelLogoutController {

    private final WebSessionManager webSessionManager;
    private final LettuceConnectionFactory lettuce;

    /**
     * Manually invalidate all sessions for the given sid. Note that the sessions are set elsewhere through Spring, see {@code IdportenSecurityConfig}.
     *
     * @param sid sid to invalidate.
     * @return Mono<Void> to indicate completion.
     */
    @GetMapping()
    Mono<Void> logout(@RequestParam String sid) {

        var sessionIds = getAllSessionIds();
        log.info("Logout request for sid: {}, found {} session(s)", sid, sessionIds.size());
        var manager = (DefaultWebSessionManager) this.webSessionManager;
        var mapped = sessionIds
                .stream()
                .map(sessionId -> manager.getSessionStore().retrieveSession(sessionId))
                .toList();
        log.info("Invalidating {} session(s)", mapped.size());
        return Flux
                .concat(mapped)
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
                .doOnEach(signal -> log.info("Invalidating session {}", Optional
                        .ofNullable(signal.get())
                        .map(WebSession::getId)
                        .orElse("?")))
                .flatMap(WebSession::invalidate)
                .then();

    }

    private List<String> getAllSessionIds() {
        var keys = Optional
                .ofNullable(lettuce
                        .getConnection()
                        .keyCommands()
                        .keys("*".getBytes(StandardCharsets.UTF_8)))
                .orElse(Set.of());
        return keys
                .stream()
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .map(session -> session.split(":")[session.split(":").length - 1])
                .toList();
    }

}