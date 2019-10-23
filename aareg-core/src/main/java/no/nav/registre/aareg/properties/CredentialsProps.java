package no.nav.registre.aareg.properties;

import static no.nav.registre.aareg.properties.Environment.PREPROD;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "credentials")
public class CredentialsProps {

    private PreprodEnv preprodEnv = new PreprodEnv();
    private TestEnv testEnv = new TestEnv();

    public String getUsername(Environment env) {
        return "srvdolly";
//        return env == PREPROD ? getPreprodEnv().getUsername() : getTestEnv().getUsername();
    }

    public String getPassword(Environment env) {
        return "0z1sMeNEjAXN1Hd";
//        return env == PREPROD ? getPreprodEnv().getPassword() : getTestEnv().getPassword();
    }

    @Getter
    @Setter
    public static class PreprodEnv {

        private String username;
        private String password;

    }

    @Getter
    @Setter
    public static class TestEnv {

        private String username;
        private String password;

    }
}
