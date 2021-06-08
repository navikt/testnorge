package no.nav.registre.testnorge.oversiktfrontend.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final AccessTokenService accessTokenService;

    @GetMapping(value = "{scope}/token/on-behalf-of")
    public ResponseEntity<TokenDTO> helloWorld(@PathVariable("scope") String scope) {
        AccessToken accessToken = accessTokenService.generateToken(
                new AccessScopes("api://" + scope + "//.default")
        ).block();

        if (accessToken.getTokenValue() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new TokenDTO(accessToken));
    }
}
