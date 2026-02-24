package no.nav.registre.testnorge.sykemelding;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Import({ ApplicationCoreConfig.class })
@SpringBootApplication
public class SykemeldingApiApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SykemeldingApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    public ApplicationInfo systemInfo(
            @Value("${spring.application.name}") String name,
            @Value("${spring.application.version}") String version
    ) {
        return ApplicationInfo
                .builder()
                .name(name)
                .version(version)
                .build();
    }

}