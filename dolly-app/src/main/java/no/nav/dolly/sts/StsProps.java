package no.nav.dolly.sts;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security-token-service")
public class StsProps {

    private String host;
    private String username;
    private String password;

}
