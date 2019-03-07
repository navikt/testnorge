package no.nav.dolly.sts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class STSConfig {

    @Value("${securityTokenService.url}")
    private String stsUrl;

    @Value("${app.credentials.serviceuser.username}")
    private String serviceUsername;

    @Value("${app.credentials.serviceuser.password}")
    private String serviceUserPassword;
}
