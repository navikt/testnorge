package no.nav.dolly.synt.dagpenger.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Syntetisering - Dagpenger",
                description = "Applikasjonen genererer syntetiske dagpengevedtak som kan settes inn i arena.",
                version = "1.0",
                termsOfService = "https://www.nav.no/",
                contact = @Contact(name = "Team Dolly", url = "slack://channel?id=CA3P9NGA2&team=T5LNAMWNA"),
                license = @License(name = "Super Strict License", url = "https://opensource.org/licenses/super-strict-license")
        ),
        servers = @Server(url = "/api/v1"),
        tags = @Tag(
                name = "Syntetisering",
                externalDocs = @ExternalDocumentation(
                        description = "Dokumentasjon",
                        url = "https://navikt.github.io/testnorge-syntetiseringspakker/apps/arena/synt_dagpenger/docs/Implementasjon.html"
                )
        )
)
@SecurityScheme(
        name = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        extensions = @Extension(properties = @ExtensionProperty(name = "x-bearerInfoFunc", value = "libs.security.decode_token"))
)
public class OpenApiConfig implements WebFilter {

    @Bean
    OpenApiCustomizer apiV1PathNormalizer() {

        return openApi -> {

            var paths = openApi.getPaths();
            if (paths == null || paths.isEmpty()) {
                return;
            }

            var normalized = new LinkedHashMap<String, PathItem>();
            for (var entry : paths.entrySet()) {
                var path = entry.getKey();
                if (path.equals("/api/v1")) {
                    normalized.put("/", entry.getValue());
                } else if (path.startsWith("/api/v1/")) {
                    normalized.put(path.substring("/api/v1".length()), entry.getValue());
                } else {
                    normalized.put(path, entry.getValue());
                }
            }

            openApi.setPaths(new Paths());
            normalized.forEach((path, item) -> openApi.getPaths().addPathItem(path, item));

        };

    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/swagger")) {
            return chain
                    .filter(exchange.mutate()
                            .request(exchange.getRequest()
                                    .mutate().path("/swagger-ui.html").build())
                            .build());
        }

        return chain.filter(exchange);
    }

}





