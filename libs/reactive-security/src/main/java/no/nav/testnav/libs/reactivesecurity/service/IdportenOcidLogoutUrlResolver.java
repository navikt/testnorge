package no.nav.testnav.libs.reactivesecurity.service;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import no.nav.testnav.libs.reactivesecurity.domain.WellKnownConfig;

public class IdportenOcidLogoutUrlResolver implements OcidLogoutUriResolver {

    private final WebClient webClient;
    private final String wellKnownUrl;

    public IdportenOcidLogoutUrlResolver(String wellKnownUrl) {
        this.webClient = WebClient.builder().build();
        this.wellKnownUrl = wellKnownUrl;
    }


    @Override
    public Mono<URI> generateUrl(DefaultOidcUser user) {
        return webClient
                .get()
                .uri(wellKnownUrl)
                .retrieve()
                .bodyToMono(WellKnownConfig.class)
                .map(config -> URI.create(config.getEndSessionEndpoint() + "?id_token_hint=" + user.getIdToken().getTokenValue()));
    }
}