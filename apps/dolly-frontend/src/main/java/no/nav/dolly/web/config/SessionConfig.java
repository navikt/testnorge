package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableSpringWebSession
@RequiredArgsConstructor
public class SessionConfig {

    private final SessionProperties sessionProperties;

    @Bean
    public ReactiveSessionRepository reactiveSessionRepository() {
        ReactiveMapSessionRepository sessionRepository = new ReactiveMapSessionRepository(new ConcurrentHashMap<>());
        int defaultMaxInactiveInterval = (int)  sessionProperties.getTimeout().toSeconds();
        sessionRepository.setDefaultMaxInactiveInterval(defaultMaxInactiveInterval);
        log.info("Set in-memory session defaultMaxInactiveInterval to {} seconds.", defaultMaxInactiveInterval);
        return sessionRepository;
    }
}