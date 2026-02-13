package no.nav.organisasjonforvalter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import no.nav.testnav.libs.reactivecore.config.ApplicationProperties;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

@Configuration
@Import(ApplicationProperties.class)
public class OpenApiConfig implements WebFilter {

    @Bean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {
        final String bearerAuth = "bearerAuth";
        final String userJwt = "user-jwt";
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(bearerAuth,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization"))
                        .addSecuritySchemes(userJwt,
                                new SecurityScheme()
                                        .name(UserConstant.USER_HEADER_JWT)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(bearerAuth, Arrays.asList("read", "write"))
                                .addList(userJwt, Arrays.asList("read", "write"))
                )
                .info(new Info()
                        .title(applicationProperties.getName())
                        .version(applicationProperties.getVersion())
                        .description(applicationProperties.getDescription())
                        .termsOfService("https://nav.no")
                        .contact(new Contact()
                                .url("https://nav-it.slack.com/archives/CA3P9NGA2")
                                .email("dolly@nav.no")
                                .name("Team Dolly")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        ));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/swagger")) {
            exchange.getResponse().setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            exchange.getResponse().getHeaders().setLocation(URI.create("/swagger-ui.html"));
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}