package no.nav.registre.inntekt.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CredentialProps {

    @Value("${testnorge-inntekt.srvuser.username.dev}")
    private String usernamePreprod;

    @Value("${testnorge-inntekt.srvuser.password.dev}")
    private String passwordPreprod;

    @Value("${testnorge-inntekt.token.url.dev}")
    private String tokenUrlPreprod;

    public String getUsername(Environment env) {
        if (env == Environment.PREPROD) {
            return usernamePreprod;
        }

        return "";
    }

    public String getPassword(Environment env) {
        if (env == Environment.PREPROD) {
            return passwordPreprod;
        }

        return "";
    }

    public String getTokenUrl(Environment env) {
        if (env == Environment.PREPROD) {
            return tokenUrlPreprod;
        }

        return "";
    }
}
