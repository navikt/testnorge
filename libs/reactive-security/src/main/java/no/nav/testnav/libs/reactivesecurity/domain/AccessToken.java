package no.nav.testnav.libs.reactivesecurity.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonAlias("access_token")
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
