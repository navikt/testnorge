package no.nav.testnav.libs.securitycore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Token {
    String accessTokenValue;
    String refreshTokenValue;
    String userId;
    boolean clientCredentials;
    Instant expiresAt;
}