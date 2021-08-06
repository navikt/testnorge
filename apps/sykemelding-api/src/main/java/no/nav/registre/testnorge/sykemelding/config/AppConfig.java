package no.nav.registre.testnorge.sykemelding.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Bean
    public ApplicationInfo systemInfo(
            @Value("${application.name}") String name,
            @Value("${application.version}") String version
    ) {
        return ApplicationInfo
                .builder()
                .name(name)
                .version(version)
                .build();
    }
}
