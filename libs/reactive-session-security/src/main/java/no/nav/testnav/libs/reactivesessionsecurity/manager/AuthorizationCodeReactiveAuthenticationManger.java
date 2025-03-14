package no.nav.testnav.libs.reactivesessionsecurity.manager;


import com.nimbusds.jose.jwk.JWK;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.web.reactive.function.client.WebClient;

public class AuthorizationCodeReactiveAuthenticationManger extends OidcAuthorizationCodeReactiveAuthenticationManager {

    public AuthorizationCodeReactiveAuthenticationManger(WebClient webClient, JWK jwk) {
        super(getTokenResponseClient(webClient, jwk), new OidcReactiveOAuth2UserService());
    }

    private static ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> getTokenResponseClient(WebClient webClient, JWK jwk) {
        var tokenResponseClient = new WebClientReactiveTokenResponseClient(webClient);
        tokenResponseClient.setConverter(new NimbusJwtClientAuthenticationParametersConverter<>((clientRegistration) -> jwk));
        return tokenResponseClient;
    }

}
