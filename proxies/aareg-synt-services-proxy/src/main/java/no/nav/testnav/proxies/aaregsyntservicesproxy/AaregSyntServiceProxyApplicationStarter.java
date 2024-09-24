package no.nav.testnav.proxies.aaregsyntservicesproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class AaregSyntServiceProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(AaregSyntServiceProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/**").uri("http://aareg-synt-services.arbeidsforhold.svc.nais.local"))
                .build();
    }
}