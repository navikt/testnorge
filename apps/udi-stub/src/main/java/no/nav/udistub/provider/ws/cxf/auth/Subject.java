package no.nav.udistub.provider.ws.cxf.auth;

import lombok.Value;
import lombok.experimental.Wither;
import no.nav.sbl.util.StringUtils;

import java.util.Optional;

import static java.util.Optional.empty;

@Wither
@Value
public class Subject {

    private final String uid;
    private final IdentType identType;
    private final SsoToken ssoToken;

    public Subject(String uid, IdentType identType, SsoToken ssoToken) {
        this.uid = uid;
        this.identType = identType;
        this.ssoToken = ssoToken;
    }

    public Optional<String> getSsoToken(SsoToken.Type type) {
        return ssoToken.getType() == type ? StringUtils.of(ssoToken.getToken()) : empty();
    }

}
