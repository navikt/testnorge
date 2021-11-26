package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.web.provider.web.utils.ThreadUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile("prod")
public class FrontChannelLogoutProdController {

    private final ReactiveRedisSessionRepository sessionRepository;

    @GetMapping()
    public Mono<Void> logout(@RequestParam String sid) {
        var redisOperations = sessionRepository.getSessionRedisOperations();
        return redisOperations.delete(redisOperations.scan(ScanOptions.NONE)
                .flatMap(sessionKey -> Mono.zip(Mono.just(sessionKey), sessionContainsSid(sessionKey, sid)))
                .filter(Tuple2::getT2)
                .map(Tuple2::getT1)
                .collectList()
                .flatMapMany(Flux::fromIterable)
        ).then();
    }

    @GetMapping("/test")
    public Mono<Void> testLogout() {
        var redisOperations = sessionRepository.getSessionRedisOperations();
        return redisOperations.scan(ScanOptions.NONE)
                        .flatMap(this::expireSessionWithSid)
                .then();
    }

    private Mono<Void> expireSessionWithSid(String sessionKey) {
        return Mono.just((Session) ThreadUtil.Instance().resolve(sessionRepository.findById(sessionKey)))
                .flatMap(session -> Mono.zip(Mono.just(session), session.getAttribute("SPRING_SECURITY_CONTEXT")))
                .filter(tuple -> ObjectUtils.isEmpty(tuple.getT2()))
                .map(Tuple2::getT1)
                .map(session -> {
                    session.setLastAccessedTime(Instant.now().minusSeconds(3000));
                    session.setMaxInactiveInterval(Duration.ofMillis(1));
                    return session;
                }).then();

    }

    private Mono<Boolean> sessionContainsSid(String sessionKey, String sid) {
        return Mono.just((Session) ThreadUtil.Instance().resolve(sessionRepository.findById(sessionKey)))
                .map(session -> (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT"))
                .map(securityContext -> (DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                .map(principal -> Objects.equals(sid, principal.getClaims().get("sid")));
    }
}
