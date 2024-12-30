package no.nav.testnav.libs.reactivesecurity.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@Slf4j
abstract class JwtResolver {

    Mono<Authentication> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new JwtResolverException("ReactiveSecurityContext is empty")))
                .doOnNext(context -> log.info("JwtResolver context.authentication {} {}", context.getAuthentication().getClass().getCanonicalName(), context.getAuthentication()))
                .map(SecurityContext::getAuthentication);
    }
}
