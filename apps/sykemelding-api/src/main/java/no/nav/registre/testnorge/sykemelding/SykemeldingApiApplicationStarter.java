package no.nav.registre.testnorge.sykemelding;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
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
        new SpringApplicationBuilder(SykemeldingApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}
