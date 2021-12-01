package no.nav.testnav.apps.oversiktfrontend.dto;

import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.libs.securitycore.domain.AccessToken;


@Value
@NoArgsConstructor(force = true)
public class TokenDTO {
    String token;

    public TokenDTO(AccessToken accessToken) {
        this.token = accessToken.getTokenValue();
    }
}
