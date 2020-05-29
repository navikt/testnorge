package no.nav.registre.aareg.properties;

import static no.nav.registre.aareg.properties.Environment.PREPROD;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CredentialsProps {

    @Value("${testnorge-aareg.srvuser.username.dev}")
    private String usernamePreprod;

    @Value("${testnorge-aareg.srvuser.password.dev}")
    private String passwordPreprod;

    @Value("${testnorge-aareg.srvuser.username.test}")
    private String usernameTest;

    @Value("${testnorge-aareg.srvuser.password.test}")
    private String passwordTest;

    public String getUsername(Environment env) {
        return env == PREPROD ? usernamePreprod : usernameTest;
    }

    public String getPassword(Environment env) {
        return env == PREPROD ? passwordPreprod : passwordTest;
    }
}
