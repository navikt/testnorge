package no.nav.registre.testnorge.oversiktfrontend.provider;

import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;

@Value
@NoArgsConstructor(force = true)
public class TokenDTO {
    public TokenDTO(AccessToken accessToken) {
        this.token = accessToken.getTokenValue();
    }

    String token;
}
