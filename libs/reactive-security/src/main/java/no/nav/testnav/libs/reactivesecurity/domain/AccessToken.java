package no.nav.testnav.libs.reactivesecurity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

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

    public String getTokenValue() {
        return access_token;
    }
}
