package no.nav.testnav.apps.brukerservice.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.securitycore.config.UserConstant;

@Service
public class JwtService {
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final CryptographyService cryptographyService;
    private final String secretKey;
    private final  String issuer;

    public JwtService(
            GetAuthenticatedUserId getAuthenticatedUserId,
            CryptographyService cryptographyService,
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${TOKEN_X_CLIENT_ID}") String issuer) {
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.cryptographyService = cryptographyService;
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    public Mono<String> getToken(User user) {
        return getAuthenticatedUserId
                .call()
                .map(userId -> cryptographyService.createId(userId, user.getOrganisasjonsnummer()))
                .map(id -> id.equals(user.getId())
                        ? Mono.empty()
                        : Mono.error(new JwtIdMismatchException(user.getId(), id))
                )
                .then(Mono.just(encodeJwt(user)));
    }


    private String encodeJwt(User user) {
        var date = Calendar.getInstance();
        return JWT
                .create()
                .withIssuer(issuer)
                .withClaim(UserConstant.USER_CLAIM_ID, user.getId())
                .withClaim(UserConstant.USER_CLAIM_USERNAME, user.getBrukernavn())
                .withClaim(UserConstant.USER_CLAIM_ORG, user.getOrganisasjonsnummer())
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(date.getTimeInMillis() + (2 * 60 * 60 * 1000)))
                .sign(Algorithm.HMAC256(secretKey));
    }


    public Mono<DecodedJWT> verify(String jwt, String id) {
        return getAuthenticatedUserId.call().map(ident -> {
            var verifier = JWT
                    .require(Algorithm.HMAC256(secretKey))
                    .withClaim(UserConstant.USER_CLAIM_ID, id)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(jwt);
        });
    }


}
