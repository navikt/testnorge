package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAuthenticatedUserId extends JwtResolver implements Callable<Mono<String>> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Override
    public Mono<String> call() {
        return getAuthenticatedResourceServerType
                .call()
                .flatMap(serverType -> switch (serverType) {
                    case TOKEN_X -> getTokenAttribute("pid");
                    case AZURE_AD -> getTokenAttribute("oid");
                });
    }

    private Mono<String> getTokenAttribute(String attribute) {

        return getJwtAuthenticationToken()
                .map(authentication ->

                        switch (authentication) {

                            case JwtAuthenticationToken jwtAuthenticationToken ->
                                    jwtAuthenticationToken.getTokenAttributes().get(attribute).toString();

                            case OAuth2AuthenticationToken oauth2AuthenticationToken ->
                                    oauth2AuthenticationToken.getPrincipal().getAttributes().get("pid").toString();

                            default -> "";
                        }
                );
    }
}
