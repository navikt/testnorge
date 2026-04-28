package no.nav.testnav.libs.securitycore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

    @JsonProperty("access_token")
    private String accessToken;

    public AccessToken(String token) {
        this.accessToken = token;
    }

    public AccessToken() {
    }

    @JsonIgnore
    public String getTokenValue() {
        return accessToken;
    }
}
