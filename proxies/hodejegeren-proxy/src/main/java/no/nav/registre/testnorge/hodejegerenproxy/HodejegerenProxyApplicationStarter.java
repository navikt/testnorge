package no.nav.registre.testnorge.hodejegerenproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HodejegerenProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(HodejegerenProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/api/**").uri("https://testnorge-hodejegeren.dev.intern.nav.no"))
                .build();
    }
}