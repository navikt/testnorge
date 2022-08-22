package no.nav.testnav.libs.reactivesessionsecurity.resolver.logut;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import reactor.core.publisher.Mono;

import java.net.URI;

@FunctionalInterface
public interface OcidLogoutUriResolver {
    Mono<URI> generateUrl(DefaultOidcUser user, String state);
}
