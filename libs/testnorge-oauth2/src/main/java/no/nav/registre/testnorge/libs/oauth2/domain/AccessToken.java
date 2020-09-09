package no.nav.registre.testnorge.libs.oauth2.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonProperty("access_token")
    private final String accessToken;

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Used by jackson
     */
    public AccessToken() {
        accessToken = null;
    }

    public String getTokenValue() {
        return accessToken;
    }
}
