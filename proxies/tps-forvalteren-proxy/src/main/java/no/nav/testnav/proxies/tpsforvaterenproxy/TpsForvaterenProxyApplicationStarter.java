package no.nav.testnav.proxies.tpsforvaterenproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
})
@SpringBootApplication
public class TpsForvaterenProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(TpsForvaterenProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, @Value("${proxy.url}") String url) {
        return builder.routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(filterSpec -> filterSpec.removeRequestHeader("Origin"))
                        .uri(url)
                ).build();
    }
}