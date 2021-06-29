package no.nav.testnav.proxies.syntrestproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.proxyconfig.config.DevConfig;
import no.nav.testnav.libs.proxyconfig.config.SecurityConfig;
import no.nav.testnav.libs.proxyconfig.router.InternalHandler;
import no.nav.testnav.libs.proxyconfig.router.InternalRouter;

@Import({
        DevConfig.class,
        SecurityConfig.class,
        InternalHandler.class,
        InternalRouter.class
})
@SpringBootApplication
public class SyntrestProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(SyntrestProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/**").uri("https://syntrest.dev.intern.nav.no"))
                .build();
    }


}
