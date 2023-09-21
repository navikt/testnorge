package no.nav.registre.testnorge.sykemelding.config;

import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

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
