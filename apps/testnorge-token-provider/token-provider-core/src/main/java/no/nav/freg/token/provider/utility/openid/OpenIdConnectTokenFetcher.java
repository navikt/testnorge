package no.nav.freg.token.provider.utility.openid;

import lombok.RequiredArgsConstructor;
import no.nav.freg.token.provider.utility.common.OpenIdConnectException;
import no.nav.freg.token.provider.utility.common.RestConsumer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
class OpenIdConnectTokenFetcher {

    private final OpenIdConnect openIdConnect;

    private final RestConsumer rest;

    Token getTokenForAuthorizationCode(String authorizationCode) {
        try {
            return rest.post(tokenUrl(), tokenRequest(authorizationCode), Token.class);
        } catch (RestClientException e) {
            throw new OpenIdConnectException("Feil ved henting av token mot OpenAM.", e);
        } catch (UnsupportedEncodingException e) {
            throw new OpenIdConnectException("Ugyldig format på redirect_uri", e);
        }
    }

    private String tokenUrl() {
        return UriComponentsBuilder.fromHttpUrl(openIdConnect.getIssoHost()).path("/access_token").build().encode().toUriString();
    }

    private HttpEntity<?> tokenRequest(String authorizationCode) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, basicCredentials());
        headers.setCacheControl("no-cache");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(requestBody(authorizationCode), headers);
    }

    private String basicCredentials() {
        return "Basic " + DatatypeConverter.printBase64Binary(String.format("%s:%s", openIdConnect.getClientId(), openIdConnect.getClientPassword()).getBytes(UTF_8));
    }

    private String requestBody(String authorizationCode) throws UnsupportedEncodingException {
        Assert.notNull(authorizationCode, "Mangler authorization code i request-kallet mot OpenAM.");
        return UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("realm", "/")
                .queryParam("redirect_uri", URLEncoder.encode(openIdConnect.getRedirectUri(), UTF_8.name()))
                .queryParam("code", authorizationCode)
                .build().getQuery();
    }
}
