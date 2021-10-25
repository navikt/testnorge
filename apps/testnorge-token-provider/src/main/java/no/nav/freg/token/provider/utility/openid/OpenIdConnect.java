package no.nav.freg.token.provider.utility.openid;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenIdConnect {

    private String issoHost;
    private String userAuthEndpoint;
    private String clientId;
    private String clientPassword;
    private String redirectUri;
}
