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
import reactor.util.function.Tuple2;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("prod")
public class FrontChannelLogoutController {

    private final WebSessionManager webSessionManager;
    private final Jedis jedis;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        var manager = (DefaultWebSessionManager) webSessionManager;
        var store = manager.getSessionStore();

        var sessionIds = getAllSessionIds();

        return Mono.just(sessionIds)
                .flatMapMany(Flux::fromIterable)
                .map(store::retrieveSession)
                .flatMap(session -> Mono.zip(Mono.just(session), session
                        .mapNotNull(ses -> (SecurityContextImpl) ses.getAttribute("SPRING_SECURITY_CONTEXT"))
                        .map(securityContext -> (DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                        .map(principal -> Objects.equals(sid, principal.getClaims().get("sid")))))
                .filter(Tuple2::getT2)
                .flatMap(Tuple2::getT1)
                .flatMap(WebSession::invalidate)
                .then();
    }

    private List<String> getAllSessionIds() {
        return this.jedis.keys("*").stream().map(session -> session.split(":")[session.split(":").length - 1]).toList();
    }
}
