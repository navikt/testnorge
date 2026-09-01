package no.nav.testnav.apps.brukerservice.service.v1;

import com.auth0.jwt.JWT;
import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static no.nav.testnav.libs.securitycore.config.UserConstant.NAV_ORGANIZATION_NUMBER;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String BANKID_USER_ID = "fake-bankid-user-id";
    private static final String HASHED_USER_ID = "fake-hashed-bankid-user-id";
    private static final String AZURE_USER_ID = "00000000-0000-0000-0000-000000000000";

    @Mock
    private GetAuthenticatedUserId getAuthenticatedUserId;

    @Mock
    private GetAuthenticatedToken getAuthenticatedToken;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private CryptographyService cryptographyService;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                getAuthenticatedUserId,
                getAuthenticatedToken,
                getUserInfo,
                cryptographyService,
                "secret",
                "issuer");
    }

    @Test
    void shouldIssueIdportenUserJwtWithHashedUserId() {
        var entity = new UserEntity();
        entity.setId(HASHED_USER_ID);
        entity.setBrukernavn("brukernavn");
        entity.setOrganisasjonsnummer(NAV_ORGANIZATION_NUMBER);

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BANKID_USER_ID));
        when(cryptographyService.createId(BANKID_USER_ID, NAV_ORGANIZATION_NUMBER)).thenReturn(HASHED_USER_ID);

        StepVerifier.create(jwtService.getToken(new User(entity)))
                .assertNext(token -> {
                    var decodedJwt = JWT.decode(token);
                    var id = decodedJwt.getClaim(UserConstant.USER_CLAIM_ID).asString();
                    var username = decodedJwt.getClaim(UserConstant.USER_CLAIM_USERNAME).asString();
                    var organizationNumber = decodedJwt.getClaim(UserConstant.USER_CLAIM_ORG).asString();

                    assertThat(id).isEqualTo(HASHED_USER_ID).isNotEqualTo(BANKID_USER_ID);
                    assertThat(IdentValidCheck.isIdentValid(Set.of(id))).isEmpty();
                    assertThat(IdentValidCheck.isIdentValid(Set.of(username))).isEmpty();
                    assertThat(IdentValidCheck.isIdentValid(Set.of(organizationNumber))).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectIdportenUserIdMismatch() {
        var entity = new UserEntity();
        entity.setId(HASHED_USER_ID);
        entity.setBrukernavn("brukernavn");
        entity.setOrganisasjonsnummer(NAV_ORGANIZATION_NUMBER);

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BANKID_USER_ID));
        when(cryptographyService.createId(BANKID_USER_ID, NAV_ORGANIZATION_NUMBER))
                .thenReturn("different-hashed-user-id");

        StepVerifier.create(jwtService.getToken(new User(entity)))
                .expectErrorMatches(error ->
                        error instanceof JwtIdMismatchException &&
                                !error.getMessage().contains(BANKID_USER_ID) &&
                                !error.getMessage().contains(HASHED_USER_ID) &&
                                !error.getMessage().contains("different-hashed-user-id"))
                .verify();
    }

    @Test
    void shouldFailClosedWhenAuthenticatedIdportenUserIsMissing() {
        when(getAuthenticatedUserId.call()).thenReturn(Mono.empty());

        StepVerifier.create(jwtService.getToken(user(HASHED_USER_ID, "brukernavn")))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldFailClosedWhenAuthenticatedUserIsMissingDuringVerification() {
        when(getAuthenticatedUserId.call()).thenReturn(Mono.empty());

        StepVerifier.create(jwtService.verify("fake-user-jwt", HASHED_USER_ID))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldIssueAzureUserJwtWithAuthenticatedUserIdAndStaticOrganization() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.just(azureUserInfo("Azure User")));

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .assertNext(token -> {
                    var decodedJwt = JWT.decode(token);

                    assertThat(decodedJwt.getClaim(UserConstant.USER_CLAIM_ID).asString()).isEqualTo(AZURE_USER_ID);
                    assertThat(decodedJwt.getClaim(UserConstant.USER_CLAIM_USERNAME).asString()).isEqualTo("Azure User");
                    assertThat(decodedJwt.getClaim(UserConstant.USER_CLAIM_ORG).asString()).isEqualTo(NAV_ORGANIZATION_NUMBER);
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectAzureClientCredentials() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(true)
                .build()));

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldRejectMissingAzureToken() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.empty());

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldRejectAzureUserIdMismatch() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.just(azureUserInfo("Azure User")));

        StepVerifier.create(jwtService.getAzureToken("different-user-id"))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldRejectMissingAzureUserInfo() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.empty());

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldRejectBlankAzureUsername() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.just(azureUserInfo(" ")));

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    void shouldRejectAzureUserIdContainingValidPersonIdentifier() {
        var personIdentifier = "41010100044";
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.just(new UserInfoExtended(
                personIdentifier,
                NAV_ORGANIZATION_NUMBER,
                "issuer",
                "Azure User",
                "user@nav.no",
                false,
                List.of())));

        StepVerifier.create(jwtService.getAzureToken(personIdentifier))
                .expectErrorMatches(error ->
                        error instanceof AccessDeniedException &&
                                !error.getMessage().contains(personIdentifier))
                .verify();
    }

    @Test
    void shouldNeverIssueAzureUserJwtContainingValidPersonIdentifier() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder()
                .clientCredentials(false)
                .build()));
        when(getUserInfo.call()).thenReturn(Mono.just(azureUserInfo("User 41010100044")));

        StepVerifier.create(jwtService.getAzureToken(AZURE_USER_ID))
                .expectErrorMatches(error ->
                        error instanceof AccessDeniedException &&
                                !error.getMessage().contains("41010100044"))
                .verify();
    }

    private static UserInfoExtended azureUserInfo(String username) {
        return new UserInfoExtended(
                AZURE_USER_ID,
                NAV_ORGANIZATION_NUMBER,
                "issuer",
                username,
                "user@nav.no",
                false,
                List.of());
    }

    private static User user(String id, String username) {
        var entity = new UserEntity();
        entity.setId(id);
        entity.setBrukernavn(username);
        entity.setOrganisasjonsnummer(NAV_ORGANIZATION_NUMBER);
        return new User(entity);
    }
}
