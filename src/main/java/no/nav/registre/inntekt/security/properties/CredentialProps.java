package no.nav.registre.inntekt.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CredentialProps {

    @Value("${testnorge-inntekt.srvuser.username.dev}")
    private String usernamePreprod;
    @Value("${testnorge-inntekt.srvuser.password.dev}")
    private String passwordPreprod;
    @Value("${testnorge-inntekt.token.url.dev}")
    private String tokenUrlPreprod;

    @Value("${testnorge-inntekt.srvuser.username.test}")
    private String usernameTest;
    @Value("${testnorge-inntekt.srvuser.password.test}")
    private String passwordTest;
    @Value("${testnorge-inntekt.token.url.test}")
    private String tokenUrlTest;

    public String getUsername(Environment env) {
        if (env == Environment.PREPROD) {
            return usernamePreprod;
        } else if (env == Environment.TEST) {
            return usernameTest;
        }
        return "";
    }

    public String getPassword(Environment env) {
        if (env == Environment.PREPROD) {
            return passwordPreprod;
        } else if (env == Environment.TEST) {
            return passwordTest;
        }
        return "";
    }

    public String getTokenUrl(Environment env) {
        if (env == Environment.PREPROD) {
            return tokenUrlPreprod;
        } else if (env == Environment.TEST) {
            return tokenUrlTest;
        }
        return "";
    }
}
