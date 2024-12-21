package no.nav.testnav.apps.endringsmeldingfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
class CustomOidcUserService extends OidcUserService {

    private final BiFunction<OidcUserRequest, OidcUserInfo, OidcUser> oidcUserMapper = CustomOidcUserService::getUser;

    private static OidcUser getUser(OidcUserRequest userRequest, OidcUserInfo userInfo) {
        var authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        var token = userRequest.getAccessToken();
        token
                .getScopes()
                .forEach(scope -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope)));
        var providerDetails = userRequest
                .getClientRegistration()
                .getProviderDetails();
        var userNameAttributeName = providerDetails
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        return oidcUserMapper.apply(userRequest, null);
    }

}
