package no.nav.dolly.proxy.texas;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;
import java.util.List;

@Configuration
@Profile("!test")
class OpenApiConfig {

    private static final String OAUTH_SCHEME_NAME = "azure";

    @Value("${spring.security.oauth2.client.provider.azure.authorization-uri}")
    private String authorizationUrl;

    @Value("${spring.security.oauth2.client.provider.azure.token-uri}")
    private String tokenUrl;

    @Value("${springdoc.swagger-ui.oauth.scopes[0]}")
    private String scope;

    @Lazy
    @Bean
    OpenAPI openAPI() {

        var flows = new OAuthFlows()
                .authorizationCode(new OAuthFlow()
                        .authorizationUrl(authorizationUrl)
                        .tokenUrl(tokenUrl)
                        .scopes(new Scopes().addString(scope, "")));
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(flows)))
                .security(List.of(new SecurityRequirement().addList(OAUTH_SCHEME_NAME)));
    }

    @Bean
    RouterFunction<ServerResponse> swaggerRedirect() {
        return RouterFunctions
                .route()
                .GET("/swagger", request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build())
                .build();
    }
}