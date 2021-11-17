package no.nav.testnav.libs.reactivesessionsecurity.repository;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public class OidcReactiveMapSessionRepository implements ReactiveSessionRepository<MapSession> {

    private Integer defaultMaxInactiveInterval;

    private final Map<String, Session> sessions;


    public OidcReactiveMapSessionRepository(Map<String, Session> sessions) {
        if (sessions == null) {
            throw new IllegalArgumentException("sessions cannot be null");
        }
        this.sessions = sessions;
    }

    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    @Override
    public Mono<Void> save(MapSession session) {
        return Mono.fromRunnable(() -> {
            if (!session.getId().equals(session.getOriginalId())) {
                this.sessions.remove(session.getOriginalId());
            }
            this.sessions.put(session.getId(), new MapSession(session));
        });
    }

    @Override
    public Mono<MapSession> findById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(this.sessions.get(id))
                .filter((session) -> !session.isExpired())
                .map(MapSession::new)
                .switchIfEmpty(deleteById(id).then(Mono.empty())));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> this.sessions.remove(id));
    }

    @Override
    public Mono<MapSession> createSession() {
        return Mono.defer(() -> {
            MapSession result = new MapSession();
            if (this.defaultMaxInactiveInterval != null) {
                result.setMaxInactiveInterval(Duration.ofSeconds(this.defaultMaxInactiveInterval));
            }
            return Mono.just(result);
        });
    }

    public Mono<Void> deleteBySid(String sid) {
        if (sid == null) return Mono.empty();

        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            var securityContext = (SecurityContextImpl) entry.getValue().getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext == null) {
                continue;
            }
            var principal = (DefaultOidcUser) securityContext.getAuthentication().getPrincipal();
            if (Objects.equals(sid, principal.getClaims().get("sid"))) return this.deleteById(entry.getKey());
        }

        return Mono.empty();
    }
}
