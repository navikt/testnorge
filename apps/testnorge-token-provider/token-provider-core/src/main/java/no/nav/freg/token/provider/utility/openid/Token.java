package no.nav.freg.token.provider.utility.openid;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Token {

    @JsonAlias("access_token")
    private String accessToken;
    @JsonAlias("expires_in")
    private Long expiresIn;
    @JsonAlias("id_token")
    private String idToken;
    @JsonAlias("refresh_token")
    private String refreshToken;
    private String scope;
    @JsonAlias("token_type")
    private String tokenType;
}
