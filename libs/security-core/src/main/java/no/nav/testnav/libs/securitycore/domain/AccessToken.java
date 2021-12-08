package no.nav.testnav.libs.securitycore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonProperty
    private final String access_token;

    public AccessToken(String token) {
        this.access_token = token;
    }

    /**
     * Used by jackson
     */
    public AccessToken() {
        access_token = null;
    }

    @JsonIgnore
    public String getTokenValue() {
        return access_token;
    }
}
