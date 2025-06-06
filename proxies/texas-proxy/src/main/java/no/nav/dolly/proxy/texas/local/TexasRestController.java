package no.nav.dolly.proxy.texas.local;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.Texas;
import no.nav.dolly.libs.texas.TexasToken;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Simple controller to test the Texas proxy locally.
 */
@RestController
@RequestMapping("/local")
@Profile("local")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TexasRestController {

    private final Texas texas;

    @PostMapping("/api/v1/token")
    @Operation(summary = "Get a token. Emulates a call to actual Texas.")
    Mono<TexasToken> token(@RequestBody GetRequest request) {
        verify(request.identity_provider);
        return texas.get(request.target);
    }

    @PostMapping("/api/v1/token/exchange")
    @Operation(summary = "Exchange a token. Emulates a call to actual Texas.")
    Mono<TexasToken> exchange(@RequestBody ExchangeRequest request) {
        verify(request.identity_provider);
        return texas.exchange(request.target, request.user_token);
    }

    @PostMapping("/api/v1/introspect")
    @Operation(summary = "Introspect a token. Emulates a call to actual Texas.")
    Mono<String> introspect(@RequestBody IntrospectRequest request) {
        verify(request.identity_provider);
        return texas.introspect(request.token);
    }

    private static void verify(String identityProvider) {
        if (!"azuread".equals(identityProvider)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identity_provider %s in request; expected azuread".formatted(identityProvider));
        }
    }

    private record GetRequest(
            @Schema(defaultValue = "azuread") String identity_provider,
            @Schema(defaultValue = "api://dev-gcp.dolly.dolly-texas-proxy/.default") String target
    ) {
    }

    private record ExchangeRequest(
            @Schema(defaultValue = "azuread") String identity_provider,
            @Schema(defaultValue = "api://dev-gcp.dolly.generer-navn-service/.default") String target,
            String user_token
    ) {
    }

    private record IntrospectRequest(
            @Schema(defaultValue = "azuread") String identity_provider,
            String token
    ) {
    }

}

