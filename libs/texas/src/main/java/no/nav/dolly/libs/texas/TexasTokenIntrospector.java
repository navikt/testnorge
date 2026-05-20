package no.nav.dolly.libs.texas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A custom token introspector that uses the Texas service to validate tokens.
 * This class is responsible for taking an incoming token, sending it to Texas for validation,
 * and mapping the introspection result to a Spring Security principal.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TexasTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

    private static final String[] ALLOWED_ENDPOINTS = new String[]{
            "/error",
            "/internal/**",
            "/swagger",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    private final Texas texas;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        return texas
                .introspect(token)
                .onErrorMap(TexasException.class, e -> {
                    log.warn("Texas introspection failed for token.", e);
                    return new OAuth2IntrospectionException("Texas introspection failed", e);
                })
                .flatMap(this::parseAndValidate);
    }

    private Mono<OAuth2AuthenticatedPrincipal> parseAndValidate(String introspectionResultJson) {

        try {

            var root = objectMapper.readTree(introspectionResultJson);
            var active = Optional
                    .ofNullable(root.get("active"))
                    .map(JsonNode::asBoolean)
                    .orElse(false);

            if (!active) {
                log.warn("Token is not active according to introspection result.");
                return Mono.error(new OAuth2IntrospectionException("Token is not active"));
            }

            // Extract all claims from the introspection result.
            var attributes = new HashMap<String, Object>();
            root
                    .properties()
                    .forEach(entry -> attributes.put(entry.getKey(), entry.getValue()));

            // Build "scope" or "roles" claim in second argument, for role based authorization? Check token.
            return Mono.just(new OAuth2IntrospectionAuthenticatedPrincipal(attributes, Collections.emptyList()));

        } catch (Exception e) {
            log.error("Failed to parse introspection result JSON: {}", introspectionResultJson, e);
            return Mono.error(new OAuth2IntrospectionException("Failed to parse introspection result", e));
        }

    }


    /**
     * Applies opaque token-based resource server security to the given {@link ServerHttpSecurity} builder.
     *
     * <p>The following is configured on the builder:
     * <ul>
     *   <li>Exception handling: unauthenticated requests receive {@code 401 Unauthorized};
     *       authenticated but unauthorised requests receive {@code 403 Forbidden}.</li>
     *   <li>CSRF protection is disabled, as the resource server is stateless.</li>
     *   <li>Authorization rules: a fixed set of open endpoints (health, Swagger, error) plus any
     *       paths supplied via {@code including} are permitted without authentication;
     *       all other exchanges require a valid token.</li>
     *   <li>OAuth2 resource server with opaque token introspection backed by this introspector.</li>
     * </ul>
     *
     * <p>The returned builder may be further customised before calling {@link ServerHttpSecurity#build()}.
     *
     * @param httpSecurity The security builder to configure; must not be {@code null}.
     * @param allowed      Zero or more additional Ant-style path patterns to permit without authentication.
     * @return The configured {@link ServerHttpSecurity} builder, for further customisation or {@code .build()}.
     */
    public ServerHttpSecurity withOpaqueTokenSecurity(ServerHttpSecurity httpSecurity, String... allowed) {

        var allowedEndpoints = Optional
                .ofNullable(allowed)
                .map(paths -> Stream
                        .concat(Arrays.stream(ALLOWED_ENDPOINTS), Arrays.stream(paths))
                        .toArray(String[]::new))
                .orElse(ALLOWED_ENDPOINTS);

        return httpSecurity
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(allowedEndpoints).permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> opaque.introspector(this)));

    }

}