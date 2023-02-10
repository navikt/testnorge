package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientRegistrationIdResolver extends Oauth2AuthenticationToken {
    public Mono<String> getClientRegistrationId() {
        Mono<Authentication> authenticationMono = ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication);

        return authenticationMono.flatMap(authentication -> {
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        return oauth2AuthenticationToken(ReactiveSecurityContextHolder
                                .getContext()
                                .map(SecurityContext::getAuthentication))
                                .map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId)
                                .doOnError(throwable -> log.error("Feilet å hente Client Registration ID for auth: ", throwable));
                    }
                    return Mono.error(new RuntimeException("Feilet å hente Client Registration ID"));
                }
        );
    }
}
