package no.nav.testnav.proxies.inntektstubproxy;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class InntektstubProxyApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(InntektstubProxyApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec
                        .path("/**")
                        .and()
                        .not(not -> not.path("/internal/**"))
                        .uri("http://inntektstub.team-inntekt.svc.nais.local/"))
                .build();
    }

}
