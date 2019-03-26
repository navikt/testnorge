package no.nav.dolly.properties;

import static no.nav.dolly.properties.Environment.PREPROD;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "credentials")
public class CredentialsProps {

    private PreprodEnv preprodEnv = new PreprodEnv();
    private TestEnv testEnv = new TestEnv();

    public String getUsername(Environment env) {
        return env == PREPROD ? getPreprodEnv().getUsername() : getTestEnv().getUsername();
    }

    public String getPassword(Environment env) {
        return env == PREPROD ? getPreprodEnv().getPassword() : getTestEnv().getPassword();
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
