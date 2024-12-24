package no.nav.testnav.apps.endringsmeldingfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class CustomOidcReactiveOAuth2UserService extends OidcReactiveOAuth2UserService {

    /**
     * Stripped from {@code org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequestUtils#getUser(OidcUserRequest, OidcUserInfo)}.
     *
     * @param userRequest OIDC user request.
     * @return OIDC user.
     */
    private static OidcUser getUser(OidcUserRequest userRequest) {

        var authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.add(new OidcUserAuthority(userRequest.getIdToken(), null));
        userRequest
                .getAccessToken()
                .getScopes()
                .forEach(scope -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope)));
        var providerDetails = userRequest
                .getClientRegistration()
                .getProviderDetails();
        var userNameAttributeName = providerDetails
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), null, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), (OidcUserInfo) null);

    }

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        return Mono.just(getUser(userRequest));
    }

}
