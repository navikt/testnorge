package no.nav.testnav.libs.reactivecore.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class SwaggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    RouterFunction<ServerResponse> swaggerRedirect() {
        return RouterFunctions
                .route()
                .GET("/swagger", request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui/index.html")).build())
                .build();
    }

    @Bean
    @Profile("local")
    ApplicationListener<ApplicationReadyEvent> swaggerEndpointLogger() {
        return event -> {
            if (event.getApplicationContext() instanceof ReactiveWebServerApplicationContext context) {
                log.info("Swagger is available at http://localhost:{}/swagger", context.getWebServer().getPort());
            } else {
                log.info("Swagger is available at http://localhost:8080/swagger (guessing on port)");
            }
        };
    }

}
