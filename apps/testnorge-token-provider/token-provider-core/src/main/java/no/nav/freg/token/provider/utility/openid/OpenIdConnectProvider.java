package no.nav.freg.token.provider.utility.openid;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import no.nav.freg.token.provider.utility.authentication.Authenticator;
import no.nav.freg.token.provider.utility.common.OpenIdConnectException;
import no.nav.freg.token.provider.utility.common.RestConsumer;

@Service
@RequiredArgsConstructor
public class OpenIdConnectProvider {

    private final OpenIdConnectTokenFetcher tokenFetcher;
    private final Authenticator authenticator;
    private final OpenIdConnect openIdConnect;
    private final RestConsumer rest;

    public Token getUserToken(
            String username,
            String password
    ) {
        authenticator.authenticateUser(username, password);
        return getToken();
    }

    private Token getToken() {
        String authorizationCode = requestAuthorizationCode();
        return tokenFetcher.getTokenForAuthorizationCode(authorizationCode);
    }

    private String requestAuthorizationCode() {
        try {
            ResponseEntity<String> response = rest.get(authorizationUrl(), authorizationRequest(), String.class);
            String location = response.getHeaders().getFirst("Location");

            if (location != null) {
                return UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst("code");
            }
        } catch (RestClientException e) {
            throw new OpenIdConnectException("Feil clientid eller redirect_uri", e);
        }
        throw new OpenIdConnectException("Feil ved authorisering mot OpenAm");
    }

    private HttpEntity<Object> authorizationRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private URI authorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl(openIdConnect.getIssoHost())
                .path("/authorize")
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
                .queryParam("client_id", openIdConnect.getClientId())
                .queryParam("state", "dummy")
                .queryParam("redirect_uri", openIdConnect.getRedirectUri())
                .build().encode().toUri();
    }
}
