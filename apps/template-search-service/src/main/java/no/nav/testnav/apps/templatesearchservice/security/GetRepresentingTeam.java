package no.nav.testnav.apps.templatesearchservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import no.nav.dolly.libs.security.config.ReactiveRequestContext;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_ID;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_REPRESENTING_TEAM;

@Component
public class GetRepresentingTeam implements Callable<Mono<String>> {

    private final String secret;

    public GetRepresentingTeam(@Value("${JWT_SECRET:#{null}}") String secret) {
        this.secret = secret;
    }

    @Override
    public Mono<String> call() {

        return Mono.zip(
                        ReactiveRequestContext.getContext()
                                .map(ServerHttpRequest::getHeaders)
                                .map(headers -> Objects.requireNonNullElse(
                                        headers.getFirst(UserConstant.USER_HEADER_JWT),
                                        "")),
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .filter(JwtAuthenticationToken.class::isInstance)
                                .map(JwtAuthenticationToken.class::cast)
                                .map(JwtAuthenticationToken::getTokenAttributes))
                .flatMap(auth -> {
                    var token = auth.getT1();
                    if (isNull(token) || token.isBlank()) {
                        return Mono.empty();
                    }

                    var jwt = JWT.require(Algorithm.HMAC256(secret))
                            .build()
                            .verify(token);
                    var issuer = (String) auth.getT2().get(JwtClaimNames.ISS);
                    if (nonNull(issuer)
                            && issuer.contains("microsoftonline")
                            && !Objects.equals(auth.getT2().get("oid"), jwt.getClaim(USER_CLAIM_ID).asString())) {
                        return Mono.error(new AccessDeniedException(
                                "User-Jwt tilhører ikke autentisert Azure-bruker."));
                    }
                    return Mono.justOrEmpty(jwt.getClaim(USER_CLAIM_REPRESENTING_TEAM).asString())
                            .filter(representingTeam -> !representingTeam.isBlank());
                });
    }
}
