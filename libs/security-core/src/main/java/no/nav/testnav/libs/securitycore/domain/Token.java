package no.nav.testnav.libs.securitycore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.time.Instant;

import static java.util.Objects.nonNull;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Token {
    String accessTokenValue;
    String refreshTokenValue;
    String userId;
    boolean clientCredentials;
    Instant expiredAt;

    public void setFreshAccessToken(OAuth2AccessTokenResponse response) {
        if (nonNull(response.getRefreshToken())) {
            setRefreshTokenValue(response.getRefreshToken().getTokenValue());
        }
        setAccessTokenValue(response.getAccessToken().getTokenValue());
        setExpiredAt(response.getAccessToken().getExpiresAt());
    }
}