package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.Token;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryTokenResolver extends Oauth2AuthenticationToken implements TokenResolver {
    private final ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Override
    public Mono<Token> getToken(ServerWebExchange exchange) {
        return oauth2AuthenticationToken(ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication))
                .flatMap(oAuth2AuthenticationToken -> auth2AuthorizedClientService.loadAuthorizedClient(
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                                oAuth2AuthenticationToken.getPrincipal().getName())
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(oAuth2AuthorizedClient -> {
                                    if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt().isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                                        log.warn("Auth client har utløpt, fjerner den som authenticated");
                                        oAuth2AuthenticationToken.setAuthenticated(false);
                                        oAuth2AuthenticationToken.eraseCredentials();
                                        auth2AuthorizedClientService.removeAuthorizedClient(
                                                        oAuth2AuthorizedClient.getClientRegistration().getRegistrationId(),
                                                        oAuth2AuthenticationToken.getPrincipal().getName())
                                                .block();
                                        return null;
                                    }
                                    return Token.builder()
                                            .accessTokenValue(oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                                            .expiresAt(oAuth2AuthorizedClient.getAccessToken().getExpiresAt())
                                            .refreshTokenValue(nonNull(oAuth2AuthorizedClient.getRefreshToken()) ? oAuth2AuthorizedClient.getRefreshToken().getTokenValue() : null)
                                            .clientCredentials(false)
                                            .build();
                                }
                        ));
    }
}