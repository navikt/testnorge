package no.nav.freg.token.provider.config;

import static no.nav.freg.token.provider.utility.authentication.Authenticator.JSON_AUTH_ENDPOINT;
import static no.nav.freg.token.provider.utility.authentication.Authenticator.OAUTH2_ENDPOINT;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.freg.token.provider.utility.common.RestConsumer;
import no.nav.freg.token.provider.utility.openid.OpenIdConnect;

@Configuration
public class OpenIdConnectConfig {

    @Bean
    public OpenIdConnect openIdConnect(
            @Value("${security.oidc.host}") String host,
            @Value("${security.openam.username}") String clientId,
            @Value("${security.openam.password}") String clientPassword,
            @Value("${security.openam.redirectUri}") String redirectUri
    ) {
        return OpenIdConnect.builder().issoHost(host + OAUTH2_ENDPOINT).userAuthEndpoint(host + JSON_AUTH_ENDPOINT).clientId(clientId).clientPassword(clientPassword).redirectUri(redirectUri).build();
    }

    @Bean
    public CookieStore cookieStore() {
        return new BasicCookieStore();
    }

    @Bean
    public RestConsumer restConsumer(CookieStore cookieStore) {
        return new RestConsumer(cookieStore);
    }
}