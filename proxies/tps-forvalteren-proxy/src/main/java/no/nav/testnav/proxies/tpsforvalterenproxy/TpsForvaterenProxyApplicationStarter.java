package no.nav.testnav.proxies.tpsforvalterenproxy;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.encodeBasicAuth;

@Slf4j
@Import({
        CoreConfig.class,
        DevConfig.class,
        SecurityConfig.class,
})
@SpringBootApplication
public class TpsForvaterenProxyApplicationStarter {

    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${proxy.url}")
    private String url;

    public static void main(String[] args) {
        SpringApplication.run(TpsForvaterenProxyApplicationStarter.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        log.info("TPS-forvalteren-proxy kalles med brukernavn: {}", username);

        return builder.routes()
                .route(spec -> spec
                        .path("/**")
                        .filters(filterSpec -> filterSpec
                                .removeRequestHeader("Origin")
                                .addRequestHeader(AUTHORIZATION, encodeBasicAuth(username, password, UTF_8)))
                        .uri(url)
                ).build();
    }
}