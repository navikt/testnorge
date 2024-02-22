package no.nav.registre.testnav.inntektsmeldingservice.config;

import no.nav.registre.testnav.inntektsmeldingservice.controller.InntektsmeldingController;
import no.nav.registre.testnav.inntektsmeldingservice.service.InntektsmeldingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.web.context.annotation.RequestScope;

@EnableJpaAuditing
@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {

    @Bean
    @RequestScope
    InntektsmeldingController inntektsmeldingController(InntektsmeldingService inntektsmeldingService) {
        return new InntektsmeldingController(inntektsmeldingService);
    }

}
