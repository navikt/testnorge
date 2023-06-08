package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials;


import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-orgnummer-service")
public class OrgnummerServiceProperties extends ValidatingServerProperties {
}
