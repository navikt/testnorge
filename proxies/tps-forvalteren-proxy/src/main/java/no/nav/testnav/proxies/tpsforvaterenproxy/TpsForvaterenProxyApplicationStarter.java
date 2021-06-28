package no.nav.testnav.proxies.tpsforvaterenproxy;

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
public class TpsForvaterenProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(TpsForvaterenProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/**").uri("https://tps-forvalteren.dev.adeo.no"))
                .build();
    }

}
