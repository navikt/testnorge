package no.nav.testnav.apps.brukerservice.controller.v1;

import no.nav.testnav.apps.brukerservice.domain.User;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.apps.brukerservice.service.v1.JwtService;
import no.nav.testnav.apps.brukerservice.service.v1.UserService;
import no.nav.testnav.apps.brukerservice.service.v1.ValidateService;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static no.nav.testnav.libs.securitycore.config.UserConstant.NAV_ORGANIZATION_NUMBER;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerControllerTest {

    private static final String USER_ID = "fake-hashed-bankid-user-id";
    private static final String USER_JWT = "fake-user-jwt";

    @Mock
    private ValidateService validateService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    private BrukerController brukerController;

    @BeforeEach
    void setUp() {
        brukerController = new BrukerController(
                validateService,
                userService,
                jwtService,
                getAuthenticatedResourceServerType);
    }

    @Test
    void shouldKeepUsingExistingIdportenTokenFlowForTokenX() {
        var user = user();
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));
        when(userService.getUser(USER_ID, true)).thenReturn(Mono.just(user));
        when(validateService.validateOrganiasjonsnummerAccess(NAV_ORGANIZATION_NUMBER)).thenReturn(Mono.empty());
        when(jwtService.getToken(user)).thenReturn(Mono.just(USER_JWT));

        StepVerifier.create(brukerController.getToken(USER_ID))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(USER_JWT);
                })
                .verifyComplete();

        verify(userService).getUser(USER_ID, true);
        verify(validateService).validateOrganiasjonsnummerAccess(NAV_ORGANIZATION_NUMBER);
        verify(jwtService).getToken(user);
        verify(jwtService, never()).getAzureToken(USER_ID);
    }

    @Test
    void shouldUseAzureTokenFlowWithoutLoadingIdportenUser() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.AZURE_AD));
        when(jwtService.getAzureToken(USER_ID)).thenReturn(Mono.just(USER_JWT));

        StepVerifier.create(brukerController.getToken(USER_ID))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isEqualTo(USER_JWT);
                })
                .verifyComplete();

        verify(jwtService).getAzureToken(USER_ID);
        verify(userService, never()).getUser(USER_ID, true);
        verify(validateService, never()).validateOrganiasjonsnummerAccess(NAV_ORGANIZATION_NUMBER);
    }

    @Test
    void shouldNotIssueIdportenTokenWhenOrganizationAccessIsDenied() {
        var user = user();
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.just(ResourceServerType.TOKEN_X));
        when(userService.getUser(USER_ID, true)).thenReturn(Mono.just(user));
        when(validateService.validateOrganiasjonsnummerAccess(NAV_ORGANIZATION_NUMBER))
                .thenReturn(Mono.error(new UserHasNoAccessToOrgnisasjonException(NAV_ORGANIZATION_NUMBER)));

        StepVerifier.create(brukerController.getToken(USER_ID))
                .expectError(UserHasNoAccessToOrgnisasjonException.class)
                .verify();

        verify(jwtService, never()).getToken(user);
    }

    @Test
    void shouldFailClosedWhenResourceServerTypeCannotBeResolved() {
        when(getAuthenticatedResourceServerType.call()).thenReturn(Mono.empty());

        StepVerifier.create(brukerController.getToken(USER_ID))
                .expectErrorMatches(error ->
                        error instanceof AccessDeniedException &&
                                error.getMessage().equals("Autentiseringstype kunne ikke fastslås."))
                .verify();

        verify(jwtService, never()).getAzureToken(USER_ID);
        verify(userService, never()).getUser(USER_ID, true);
    }

    private static User user() {
        var entity = new UserEntity();
        entity.setId(USER_ID);
        entity.setBrukernavn("fake-user");
        entity.setOrganisasjonsnummer(NAV_ORGANIZATION_NUMBER);
        return new User(entity);
    }
}
