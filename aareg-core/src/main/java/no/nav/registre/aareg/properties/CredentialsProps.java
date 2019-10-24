package no.nav.registre.aareg.properties;

import static no.nav.registre.aareg.properties.Environment.PREPROD;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CredentialsProps {

    @Value("${testnorge-aareg.srvuser.username}")
    private String usernamePreprod;

    @Value("${testnorge-aareg.srvuser.password}")
    private String passwordPreprod;

    private String usernameTest = null;
    private String passwordTest = null;

    public String getUsername(Environment env) {
        return env == PREPROD ? usernamePreprod : usernameTest;
    }

    public String getPassword(Environment env) {
        return env == PREPROD ? passwordPreprod : passwordTest;
    }
}
