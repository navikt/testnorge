package no.nav.testnav.apps.brukerservice.service.v1;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {
    private static final String NAV_ORG_NUMBER = "889640782";

    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final GetAuthenticatedToken getAuthenticatedToken;
    private final GetUserInfo getUserInfo;
    private final CryptographyService cryptographyService;
    private final String secretKey;
    private final  String issuer;

    public JwtService(
            GetAuthenticatedUserId getAuthenticatedUserId,
            GetAuthenticatedToken getAuthenticatedToken,
            GetUserInfo getUserInfo,
            CryptographyService cryptographyService,
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${spring.security.oauth2.resourceserver.tokenx.accepted-audience}") String issuer) {
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.getAuthenticatedToken = getAuthenticatedToken;
        this.getUserInfo = getUserInfo;
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

    public Mono<String> getAzureToken(String id) {
        return getAuthenticatedToken.call()
                .filter(token -> !token.isClientCredentials())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure User-Jwt krever brukerkontekst.")))
                .then(Mono.defer(getUserInfo::call))
                .filter(userInfo -> Objects.equals(id, userInfo.id()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure bruker-ID samsvarer ikke med autentisert bruker.")))
                .map(userInfo -> encodeJwt(id, userInfo.brukernavn(), NAV_ORG_NUMBER));
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

    private String encodeJwt(User user) {
        return encodeJwt(user.getId(), user.getBrukernavn(), user.getOrganisasjonsnummer());
    }

    private String encodeJwt(String id, String username, String organizationNumber) {
        if (isBlank(id) || isBlank(username) || isBlank(organizationNumber)) {
            throw new AccessDeniedException("User-Jwt mangler påkrevde claims.");
        }
        if (containsValidPersonIdentifier(id)
                || containsValidPersonIdentifier(username)
                || containsValidPersonIdentifier(organizationNumber)) {
            throw new AccessDeniedException("User-Jwt kan ikke inneholde personnummer.");
        }

        var date = Calendar.getInstance();
        return JWT
                .create()
                .withIssuer(issuer)
                .withClaim(UserConstant.USER_CLAIM_ID, id)
                .withClaim(UserConstant.USER_CLAIM_USERNAME, username)
                .withClaim(UserConstant.USER_CLAIM_ORG, organizationNumber)
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(date.getTimeInMillis() + (2 * 60 * 60 * 1000)))
                .sign(Algorithm.HMAC256(secretKey));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean containsValidPersonIdentifier(String value) {
        for (var index = 0; index <= value.length() - 11; index++) {
            var candidate = value.substring(index, index + 11);
            if (!IdentValidCheck.isIdentValid(Set.of(candidate)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
