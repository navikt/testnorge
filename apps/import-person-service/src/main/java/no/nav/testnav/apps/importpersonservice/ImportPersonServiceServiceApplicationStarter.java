package no.nav.testnav.apps.importpersonservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableWebFlux
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class ImportPersonServiceServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ImportPersonServiceServiceApplicationStarter.class, args);
    }
}