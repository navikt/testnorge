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

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

/**
 * A custom token introspector that uses the Texas service to validate tokens.
 * This class is responsible for taking an incoming token, sending it to Texas for validation,
 * and mapping the introspection result to a Spring Security principal.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TexasTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

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
     * Configures the given {@link ServerHttpSecurity} for opaque token-based OAuth2 resource server authentication.
     *
     * <p>This method sets up the security configuration to:
     * <ul>
     *   <li>Use this introspector to validate incoming opaque tokens via the Texas service.</li>
     *   <li>Return HTTP 401 Unauthorized for authentication failures.</li>
     *   <li>Return HTTP 403 Forbidden for authorization failures.</li>
     * </ul>
     *
     * @param httpSecurity The {@link ServerHttpSecurity} to configure.
     * @return The configured {@link ServerHttpSecurity} with opaque token security enabled.
     */
    public ServerHttpSecurity withOpaqueTokenSecurity(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }))
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> opaque.introspector(this)));
    }

}