package no.nav.testnav.personfastedataservice;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.testnav.libs.core.config.CoreConfig;
import no.nav.testnav.libs.security.config.SecureOAuth2ServerToServerConfiguration;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableJpaAuditing
@SpringBootApplication
public class PersonFasteDataServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PersonFasteDataServiceApplicationStarter.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
