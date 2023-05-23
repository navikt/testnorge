package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientRegistrationIdResolver extends Oauth2AuthenticationToken {
    public Mono<ResourceServerType> getClientRegistrationId() {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        return oauth2AuthenticationToken(ReactiveSecurityContextHolder
                                .getContext()
                                .map(SecurityContext::getAuthentication))
                                .map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId)
                                .map(id -> "aad".equals(id) ? ResourceServerType.AZURE_AD : ResourceServerType.TOKEN_X)
                                .doOnError(throwable -> log.error("Feilet å hente Client Registration ID for auth: ", throwable));
                    }
                    return Mono.empty();
                }
        );
    }
}
