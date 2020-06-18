package no.nav.freg.token.provider.utility.authentication;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;

import no.nav.freg.token.provider.utility.common.OpenIdConnectException;
import no.nav.freg.token.provider.utility.common.RestConsumer;
import no.nav.freg.token.provider.utility.openid.OpenIdConnect;

@Service
@RequiredArgsConstructor
public class Authenticator {

    public static final String JSON_AUTH_ENDPOINT = "/json/authenticate";
    public static final String OAUTH2_ENDPOINT = "/oauth2";

    private final RestConsumer rest;

    private final CookieStore cookieStore;

    private final OpenIdConnect openIdConnect;

    public void authenticateUser(
            String username,
            String password
    ) {
        try {
            User user = rest.post(openIdConnect.getUserAuthEndpoint(), createUserAuthNegotiateRequest(), User.class);
            SessionToken sessionToken = rest.post(openIdConnect.getUserAuthEndpoint(), createUserAuthRequest(user, username, password), SessionToken.class);
            addSessionToken(sessionToken);
        } catch (Exception e) {
            throw new OpenIdConnectException("Feil oppstod ved user authentication mot OpenAM.", e);
        }
    }

    private HttpEntity<Object> createUserAuthNegotiateRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Negotiate");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Object> createUserAuthRequest(
            User user,
            String username,
            String password
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        user.setVerdi("Name", username);
        user.setVerdi("Password", password);
        return new HttpEntity<>(user, httpHeaders);
    }

    private void addSessionToken(SessionToken sessionToken) {
        cookieStore.clearExpired(new Date(System.currentTimeMillis() - 3600000L));
        BasicClientCookie cookie = new BasicClientCookie("nav-isso", sessionToken.getTokenId());
        URI uri = URI.create(openIdConnect.getIssoHost());
        cookie.setDomain("." + uri.getHost());
        cookie.setPath("/");
        cookie.setSecure(true);
        cookieStore.addCookie(cookie);
    }
}
