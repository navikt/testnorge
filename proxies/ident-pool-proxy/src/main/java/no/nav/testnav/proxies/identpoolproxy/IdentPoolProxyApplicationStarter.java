package no.nav.testnav.proxies.identpoolproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.core.config.CoreConfig;
import no.nav.testnav.libs.proxyconfig.config.DevConfig;
import no.nav.testnav.libs.proxyconfig.config.SecurityConfig;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class IdentPoolProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(IdentPoolProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/api/**").uri("https://ident-pool.dev.intern.nav.no"))
                .build();
    }
}