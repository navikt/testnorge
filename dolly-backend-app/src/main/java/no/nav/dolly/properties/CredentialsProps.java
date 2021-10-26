package no.nav.dolly.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static no.nav.dolly.properties.Environment.PREPROD;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "credentials")
public class CredentialsProps {

    private Preprod preprod = new Preprod();
    private Test test = new Test();

    public String getUsername(Environment env) {
        return env == PREPROD ? this.getPreprod().getUsername() : this.getTest().getUsername();
    }

    public String getPassword(Environment env) {
        return env == PREPROD ? this.getPreprod().getPassword() : this.getTest().getPassword();
    }

    @Getter
    @Setter
    public static class Preprod {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Test {
        private String username;
        private String password;
    }
}
