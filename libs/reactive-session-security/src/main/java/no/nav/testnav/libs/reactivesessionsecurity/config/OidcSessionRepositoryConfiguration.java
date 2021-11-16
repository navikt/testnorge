package no.nav.testnav.libs.reactivesessionsecurity.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.repository.OidcReactiveMapSessionRepository;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class OidcSessionRepositoryConfiguration {

    private final SessionProperties sessionProperties;

    @Bean
    public OidcReactiveMapSessionRepository reactiveSessionRepository() {
        OidcReactiveMapSessionRepository sessionRepository = new OidcReactiveMapSessionRepository(new ConcurrentHashMap<>());
        int defaultMaxInactiveInterval = (int) (sessionProperties.getTimeout() == null
                ? Duration.ofMinutes(30)
                : sessionProperties.getTimeout()
        ).toSeconds();
        sessionRepository.setDefaultMaxInactiveInterval(defaultMaxInactiveInterval);
        log.info("Set in-memory session max inactive to {} seconds.", defaultMaxInactiveInterval);
        return sessionRepository;
    }
}
