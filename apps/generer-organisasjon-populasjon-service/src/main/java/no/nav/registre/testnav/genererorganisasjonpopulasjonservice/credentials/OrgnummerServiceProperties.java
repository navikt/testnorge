package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.credentials;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.organisasjon-orgnummer-service")
public class OrgnummerServiceProperties extends ServerProperties {
}
