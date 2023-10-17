package no.nav.registre.testnorge.sykemelding;

import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "no.nav.registre.testnorge.sykemelding")
@Import({ ApplicationCoreConfig.class })
public class SykemeldingApiApplicationStarter {

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

    public static void main(String[] args) {
        SpringApplication.run(SykemeldingApiApplicationStarter.class, args);
    }
}