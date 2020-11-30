package no.nav.dolly.security.oauth2.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonProperty("access_token")
    private final String token;

    public AccessToken(String token) {
        this.token = token;
    }

    /**
     * Used by jackson
     */
    public AccessToken() {
        token = null;
    }

    public String getTokenValue() {
        return token;
    }
}
