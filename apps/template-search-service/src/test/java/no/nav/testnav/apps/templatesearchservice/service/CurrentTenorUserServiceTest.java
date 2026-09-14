package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalValidationException;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentTenorUserServiceTest {

    private static final String HASHED_BANK_ID = "a99799e1712f12229f9888b48f33da414dc58e803f11848653699a319ca43344";

    @Mock
    private GetAuthenticatedToken getAuthenticatedToken;

    @Mock
    private GetUserInfo getUserInfo;

    private CurrentTenorUserService currentUserService;

    @BeforeEach
    void setUp() {
        currentUserService = new CurrentTenorUserService(
                getAuthenticatedToken,
                getUserInfo,
                new TenorPersonMalValidationService(JsonMapper.builder().build()));
    }

    @Test
    void shouldUseExactHashedBankIdFromUserJwt() {
        var token = Token.builder()
                .clientCredentials(false)
                .userId("tokenx-value-must-not-be-persisted")
                .build();
        var userInfo = new UserInfoExtended(
                HASHED_BANK_ID,
                "889640782",
                "issuer",
                "BankID-bruker",
                "epost",
                true,
                List.of());

        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));
        StepVerifier.create(currentUserService.getCurrentUser())
                .assertNext(owner -> {
                    assertThat(owner.brukerId()).isEqualTo(HASHED_BANK_ID);
                    assertThat(owner.brukerId()).isNotEqualTo(token.getUserId());
                    assertThat(owner.brukertype()).isEqualTo(TenorMalBrukerType.BANKID);
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectClientCredentialsWithoutLoadingUserInfo() {
        var token = Token.builder()
                .clientCredentials(true)
                .build();
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token));

        StepVerifier.create(currentUserService.getCurrentUser())
                .expectError(AccessDeniedException.class)
                .verify();

        verify(getUserInfo, never()).call();
    }

    @Test
    void shouldRejectPlainPersonIdentifierAsUserId() {
        var token = Token.builder()
                .clientCredentials(false)
                .build();
        var userInfo = new UserInfoExtended(
                "41010100044",
                "889640782",
                "issuer",
                "BankID-bruker",
                "epost",
                true,
                List.of());
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(token));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));

        StepVerifier.create(currentUserService.getCurrentUser())
                .expectError(TenorMalValidationException.class)
                .verify();
    }
}
