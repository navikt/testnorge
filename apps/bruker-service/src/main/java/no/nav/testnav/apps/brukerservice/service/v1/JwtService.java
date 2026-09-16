package no.nav.testnav.apps.brukerservice.service.v1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.apps.brukerservice.consumer.DollyBackendConsumer;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.securitycore.config.UserConstant.NAV_ORGANIZATION_NUMBER;
import static no.nav.testnav.libs.securitycore.config.UserConstant.TEAM_BRUKER_ID_DEV_PREFIX;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_CLAIM_REPRESENTING_TEAM;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Service
public class JwtService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(2);
    private static final Pattern TEAM_BRUKER_ID_PATTERN =
            Pattern.compile("(?:" + Pattern.quote(TEAM_BRUKER_ID_DEV_PREFIX) + ")?team-bruker-id-\\d+");

    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final GetAuthenticatedToken getAuthenticatedToken;
    private final GetUserInfo getUserInfo;
    private final CryptographyService cryptographyService;
    private final DollyBackendConsumer dollyBackendConsumer;
    private final String secretKey;
    private final String issuer;

    public JwtService(
            GetAuthenticatedUserId getAuthenticatedUserId,
            GetAuthenticatedToken getAuthenticatedToken,
            GetUserInfo getUserInfo,
            CryptographyService cryptographyService,
            DollyBackendConsumer dollyBackendConsumer,
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${spring.security.oauth2.resourceserver.tokenx.accepted-audience}") String issuer) {
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.getAuthenticatedToken = getAuthenticatedToken;
        this.getUserInfo = getUserInfo;
        this.cryptographyService = cryptographyService;
        this.dollyBackendConsumer = dollyBackendConsumer;
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    public Mono<String> getToken(User user) {
        return getAuthenticatedUserId
                .call()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Autentisert bruker mangler.")))
                .map(userId -> cryptographyService.createId(userId, user.getOrganisasjonsnummer()))
                .flatMap(id -> id.equals(user.getId())
                        ? Mono.fromSupplier(() -> encodeJwt(user))
                        : Mono.error(new JwtIdMismatchException()));
    }

    public Mono<String> getAzureToken(String id) {
        return getAuthenticatedToken.call()
                .filter(token -> !token.isClientCredentials())
                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure User-Jwt krever brukerkontekst.")))
                .then(Mono.defer(getUserInfo::call))
                .filter(userInfo -> Objects.equals(id, userInfo.id()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Azure bruker-ID samsvarer ikke med autentisert bruker.")))
                .flatMap(userInfo -> dollyBackendConsumer.getRepresentererTeamBrukerId()
                        .map(representingTeam -> encodeJwt(
                                id,
                                userInfo.brukernavn(),
                                NAV_ORGANIZATION_NUMBER,
                                representingTeam))
                        .switchIfEmpty(Mono.fromSupplier(() ->
                                encodeJwt(id, userInfo.brukernavn(), NAV_ORGANIZATION_NUMBER, null))));
    }

    public Mono<DecodedJWT> verify(String jwt, String id) {
        return getAuthenticatedUserId.call()
                .switchIfEmpty(Mono.error(new AccessDeniedException("Autentisert bruker mangler.")))
                .then(Mono.fromCallable(() -> JWT
                        .require(Algorithm.HMAC256(secretKey))
                        .withClaim(UserConstant.USER_CLAIM_ID, id)
                        .withIssuer(issuer)
                        .build()
                        .verify(jwt)));
    }

    private String encodeJwt(User user) {
        return encodeJwt(user.getId(), user.getBrukernavn(), user.getOrganisasjonsnummer(), null);
    }

    private String encodeJwt(String id, String username, String organizationNumber, String representingTeam) {
        if (isBlank(id) || isBlank(username) || isBlank(organizationNumber)) {
            throw new AccessDeniedException("User-Jwt mangler påkrevde claims.");
        }
        if (containsValidPersonIdentifier(id)
                || containsValidPersonIdentifier(username)
                || containsValidPersonIdentifier(organizationNumber)) {
            throw new AccessDeniedException("User-Jwt kan ikke inneholde personnummer.");
        }

        var normalizedRepresentingTeam = validateRepresentingTeam(representingTeam);
        var issuedAt = Instant.now();
        var jwtBuilder = JWT
                .create()
                .withIssuer(issuer)
                .withClaim(UserConstant.USER_CLAIM_ID, id)
                .withClaim(UserConstant.USER_CLAIM_USERNAME, username)
                .withClaim(UserConstant.USER_CLAIM_ORG, organizationNumber)
                .withIssuedAt(issuedAt)
                .withNotBefore(issuedAt)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(issuedAt.plus(TOKEN_LIFETIME));
        if (nonNull(normalizedRepresentingTeam)) {
            jwtBuilder.withClaim(USER_CLAIM_REPRESENTING_TEAM, normalizedRepresentingTeam);
        }
        return jwtBuilder.sign(Algorithm.HMAC256(secretKey));
    }

    private static String validateRepresentingTeam(String representingTeam) {

        var normalizedRepresentingTeam = trimToNull(representingTeam);
        if (isNull(normalizedRepresentingTeam)) {
            return null;
        }
        if (normalizedRepresentingTeam.length() > 100
                || !TEAM_BRUKER_ID_PATTERN.matcher(normalizedRepresentingTeam).matches()
                || containsValidPersonIdentifier(normalizedRepresentingTeam)) {
            throw new AccessDeniedException("Ugyldig teamkontekst for User-Jwt.");
        }
        return normalizedRepresentingTeam;
    }

    private static boolean containsValidPersonIdentifier(String value) {
        for (var index = 0; index <= value.length() - 11; index++) {
            var candidate = value.substring(index, index + 11);
            if (IdentValidCheck.isIdentValid(candidate)) {
                return true;
            }
        }
        return false;
    }
}
