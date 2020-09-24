package no.nav.registre.testnorge.oversiktfrontend.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.oversiktfrontend.config.ApplicationsProperties;
import no.nav.registre.testnorge.oversiktfrontend.security.OnBehalfOfGenerateAccessTokenService;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationsController {
    private final ApplicationsProperties properties;
    private final OnBehalfOfGenerateAccessTokenService accessTokenService;

    @GetMapping
    public ResponseEntity<Set<String>> getApplications() {
        return ResponseEntity.ok(properties.getApplications().keySet());
    }

    @GetMapping(value = "{name}/token/on-behalf-of")
    public ResponseEntity<TokenDTO> helloWorld(@PathVariable("name") String name) {
        AccessToken accessToken = accessTokenService.generateToken(
                new AccessScopes("api://" + properties.getApplications().get(name) + "//.default")
        );
        return ResponseEntity.ok(new TokenDTO(accessToken));
    }
}