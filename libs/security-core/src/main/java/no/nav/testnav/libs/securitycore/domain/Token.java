package no.nav.testnav.libs.securitycore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@AllArgsConstructor
public class Token {
    String accessTokenValue;
    String refreshTokenValue;
    String userId;
    boolean clientCredentials;
    Instant expiredAt;
}
