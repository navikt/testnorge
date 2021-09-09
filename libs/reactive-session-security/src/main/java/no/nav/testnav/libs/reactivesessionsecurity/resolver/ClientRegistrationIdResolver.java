package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ClientRegistrationIdResolver extends Oauth2AuthenticationToken {
    public Mono<String> getClientRegistrationId() {
        return oauth2AuthenticationToken().map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId);
    }
}
