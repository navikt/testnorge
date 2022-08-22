package no.nav.testnav.libs.reactivesessionsecurity.resolver.logut;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import no.nav.testnav.libs.reactivesessionsecurity.domain.idporten.WellKnownConfig;


public class IdportenOcidLogoutUrlResolver implements OcidLogoutUriResolver {
    private final WebClient webClient;
    private final String wellKnownUrl;
    private final String postLogoutRedirectUri;

    public IdportenOcidLogoutUrlResolver(String wellKnownUrl, String postLogoutRedirectUri) {
        this.postLogoutRedirectUri = postLogoutRedirectUri;
        this.webClient = WebClient.builder().build();
        this.wellKnownUrl = wellKnownUrl;
    }

    @Override
    public Mono<URI> generateUrl(DefaultOidcUser user, String state) {
        return webClient
                .get()
                .uri(wellKnownUrl)
                .retrieve()
                .bodyToMono(WellKnownConfig.class)
                .map(config -> UriComponentsBuilder
                        .fromUri(URI.create(config.getEndSessionEndpoint()))
                        .queryParam("id_token_hint", user.getIdToken().getTokenValue())
                        .queryParam("post_logout_redirect_uri", postLogoutRedirectUri)
                        .queryParam("state", state)
                        .build()
                        .toUri()
                );
    }


}