package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAuthenticatedUserId implements Callable<Mono<String>> {

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

        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> getTokenAttribute(authentication, attribute));
    }

    private String getTokenAttribute(Authentication authentication, String attribute) {

        log.info("GetAuthenticatedUserId context.authentication {}, {}", authentication.getClass().getPackageName(), authentication);
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes().get(attribute).toString();

        } else if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {
            log.info("oauth2AuthenticationToken {}", oauth2AuthenticationToken.getPrincipal());
            return oauth2AuthenticationToken.getPrincipal().getAttributes().get(attribute).toString();

        } else {
            return "";
        }
    }
}
