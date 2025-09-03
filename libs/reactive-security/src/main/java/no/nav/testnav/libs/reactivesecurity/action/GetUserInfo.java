package no.nav.testnav.libs.reactivesecurity.action;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.security.config.ReactiveRequestContext;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_EMAIL;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_ID;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_ORG;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_USERNAME;

@Slf4j
@Component
public class GetUserInfo extends JwtResolver implements Callable<Mono<UserInfoExtended>> {

    private final String secret;

    public GetUserInfo(@Value("${JWT_SECRET:#{null}}") String secret) {
        this.secret = secret;
    }

    @Override
    public Mono<UserInfoExtended> call() {

        return Mono.zip(ReactiveRequestContext.getContext()
                                .map(ServerHttpRequest::getHeaders)
                                .map(headers -> nonNull(headers.getFirst(UserConstant.USER_HEADER_JWT)) ?
                                        headers.getFirst(UserConstant.USER_HEADER_JWT) : ""),
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .filter(JwtAuthenticationToken.class::isInstance)
                                .map(JwtAuthenticationToken.class::cast)
                                .map(JwtAuthenticationToken::getTokenAttributes))
                .map(auth -> { // Henter fra token

                    if (((String) auth.getT2().get(JwtClaimNames.ISS)).contains("microsoftonline")) {
                        return new UserInfoExtended(
                                (String) auth.getT2().get("oid"),
                                "889640782",
                                (String) auth.getT2().get(JwtClaimNames.ISS),
                                (String) auth.getT2().get("name"),
                                (String) auth.getT2().get("preferred_username"),
                                false,
                                auth.getT2().get("groups") instanceof List ?
                                        (List<String>) auth.getT2().get("groups") : emptyList());

                    } else { // Henter fra header
                        var jwt = JWT.decode(auth.getT1());
                        var verifier = JWT.require(Algorithm.HMAC256(secret)).build();
                        verifier.verify(jwt);
                        return new UserInfoExtended(
                                getClaim(jwt, USER_CLAIM_ID),
                                getClaim(jwt, USER_CLAIM_ORG),
                                (String) auth.getT2().get(JwtClaimNames.ISS),
                                getClaim(jwt, USER_CLAIM_USERNAME),
                                getClaim(jwt, USER_CLAIM_EMAIL),
                                true,
                                emptyList());
                    }
                });
    }

    private static String getClaim(DecodedJWT jwt, String claimName) {

        return Objects.requireNonNullElse(jwt.getClaim(claimName).asString(), "");
    }
}