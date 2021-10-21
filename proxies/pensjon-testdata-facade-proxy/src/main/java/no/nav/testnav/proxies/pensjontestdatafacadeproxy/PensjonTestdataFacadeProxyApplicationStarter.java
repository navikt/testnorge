package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class
})
@SpringBootApplication
public class PensjonTestdataFacadeProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(PensjonTestdataFacadeProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .addRequestHeader(HttpHeaders.AUTHORIZATION, "dolly")) //Auth header er required men sjekkes ikke utover det
                        .uri("https://pensjon-testdata-facade.dev.adeo.no/"))
                .build();
    }
}
